package com.example.demo.service;

import com.example.demo.dao.UserMapper;
import com.example.demo.dao.RoleMapper;
import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.Role;
import com.example.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 用户业务逻辑层
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public List<User> findAll() {
        List<User> users = userMapper.findAll();
        for (User user : users) {
            List<Role> roles = roleMapper.findByUserId(user.getId());
            user.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
            user.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.joining(", ")));
        }
        return users;
    }

    public PageResult<User> findPage(int page, int pageSize) {
        return findPage(page, pageSize, null, null);
    }

    public PageResult<User> findPage(int page, int pageSize, String username, String nickname) {
        log.info("findPage: page={}, pageSize={}, username='{}', nickname='{}'", page, pageSize, username, nickname);
        int offset = (page - 1) * pageSize;
        List<User> users = userMapper.findPage(offset, pageSize, username, nickname);
        for (User user : users) {
            List<Role> roles = roleMapper.findByUserId(user.getId());
            user.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
            user.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.joining(", ")));
        }
        long total = userMapper.countByCondition(username, nickname);
        return new PageResult<>(users, total, page, pageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(User user) {
        userMapper.insert(user);
        if (user.getRoleIds() != null) {
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        userMapper.update(user);
        userMapper.deleteUserRole(user.getId());
        if (user.getRoleIds() != null) {
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        userMapper.deleteUserRole(id);
        userMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userMapper.deleteUserRole(userId);
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                userMapper.insertUserRole(userId, roleId);
            }
        }
    }
}