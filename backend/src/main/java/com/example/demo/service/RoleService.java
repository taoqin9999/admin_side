package com.example.demo.service;

import com.example.demo.dao.RoleMapper;
import com.example.demo.dao.PermissionMapper;
import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.Permission;
import com.example.demo.pojo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
/**
 * 角色业务逻辑层
 */
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    public List<Role> findAll() {
        List<Role> roles = roleMapper.findAll();
        for (Role role : roles) {
            List<Permission> perms = permissionMapper.findByRoleId(role.getId());
            role.setPermissionIds(perms.stream().map(Permission::getId).collect(Collectors.toList()));
        }
        return roles;
    }

    public PageResult<Role> findPage(int page, int pageSize) {
        return findPage(page, pageSize, null, null);
    }

    public PageResult<Role> findPage(int page, int pageSize, String name, String memo) {
        int offset = (page - 1) * pageSize;
        List<Role> roles = roleMapper.findPage(offset, pageSize, name, memo);
        for (Role role : roles) {
            List<Permission> perms = permissionMapper.findByRoleId(role.getId());
            role.setPermissionIds(perms.stream().map(Permission::getId).collect(Collectors.toList()));
        }
        long total = roleMapper.countByCondition(name, memo);
        return new PageResult<>(roles, total, page, pageSize);
    }

    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(Role role) {
        roleMapper.insert(role);
        if (role.getPermissionIds() != null) {
            for (Long permId : role.getPermissionIds()) {
                roleMapper.insertRolePermission(role.getId(), permId);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Role role) {
        roleMapper.update(role);
        roleMapper.deleteRolePermission(role.getId());
        if (role.getPermissionIds() != null) {
            for (Long permId : role.getPermissionIds()) {
                roleMapper.insertRolePermission(role.getId(), permId);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        roleMapper.deleteRolePermission(id);
        roleMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleMapper.deleteRolePermission(roleId);
        if (permissionIds != null) {
            for (Long permId : permissionIds) {
                roleMapper.insertRolePermission(roleId, permId);
            }
        }
    }
}