package com.example.demo.shiro;

import com.example.demo.dao.PermissionMapper;
import com.example.demo.dao.RoleMapper;
import com.example.demo.pojo.Permission;
import com.example.demo.pojo.Role;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shiro 自定义 Realm
 * 负责用户身份认证和权限授权的数据获取
 */
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 授权：获取当前用户的角色和权限标识
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 获取角色标识集合
        List<Role> roleList = roleMapper.findByUsername(user.getUsername());
        Set<String> roleSet = new HashSet<>();
        for (Role r : roleList) {
            roleSet.add(r.getName());
        }
        info.setRoles(roleSet);

        // 获取权限标识集合
        List<Permission> permList = permissionMapper.findByUsername(user.getUsername());
        Set<String> permSet = new HashSet<>();
        for (Permission p : permList) {
            permSet.add(p.getCode());
        }
        info.setStringPermissions(permSet);
        return info;
    }

    /**
     * 认证：验证用户名密码是否正确
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UnknownAccountException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new LockedAccountException("账号已被锁定, 请联系管理员");
        }
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }
}