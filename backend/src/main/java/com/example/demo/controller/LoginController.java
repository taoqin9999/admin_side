package com.example.demo.controller;

import com.example.demo.pojo.Permission;
import com.example.demo.pojo.ResponseBo;
import com.example.demo.pojo.User;
import com.example.demo.service.PermissionService;
import com.example.demo.util.MD5Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录认证控制器
 * 处理用户登录、获取用户信息、退出登录等
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 处理未登录时的 GET 请求，返回 401 状态码
     * 避免 Shiro 302 重定向导致前端无法正确处理
     */
    @GetMapping("/login")
    public ResponseBo loginGet() {
        return ResponseBo.error(401, "未登录或登录已过期");
    }

    /**
     * 用户登录
     * 验证用户名和密码（MD5 加密后），登录成功返回用户信息、菜单和权限
     */
    @PostMapping("/login")
    public ResponseBo login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Boolean rememberMe = Boolean.parseBoolean(params.getOrDefault("rememberMe", "false"));

        String encryptedPwd = MD5Utils.encrypt(username, password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, encryptedPwd, rememberMe);
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(token);
            User user = (User) subject.getPrincipal();

            // 获取菜单树
            List<Permission> menus = permissionService.findMenuTreeByUsername(username);
            // 获取权限标识列表
            List<String> perms = permissionService.findPermCodesByUsername(username);

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("menus", menus);
            data.put("permissions", perms);
            return ResponseBo.ok("登录成功").putData(data);
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            return ResponseBo.error(401, "用户名或密码错误");
        } catch (LockedAccountException e) {
            return ResponseBo.error(401, e.getMessage());
        } catch (AuthenticationException e) {
            return ResponseBo.error(401, "认证失败");
        }
    }

    /**
     * 获取当前登录用户信息（含菜单和权限）
     */
    @GetMapping("/user/info")
    @RequiresAuthentication
    public ResponseBo userInfo() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        List<Permission> menus = permissionService.findMenuTreeByUsername(user.getUsername());
        List<String> perms = permissionService.findPermCodesByUsername(user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("menus", menus);
        data.put("permissions", perms);
        return ResponseBo.ok().putData(data);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResponseBo logout() {
        SecurityUtils.getSubject().logout();
        return ResponseBo.ok("退出成功");
    }

    /**
     * 无权限时返回 403
     */
    @GetMapping("/403")
    public ResponseBo noPerm() {
        return ResponseBo.error(403, "没有权限访问");
    }
}