package com.example.demo.controller;

import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.ResponseBo;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import com.example.demo.util.MD5Utils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户列表查询、新增、编辑、删除、分配角色等功能
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     * 支持用户名、昵称模糊搜索
     */
    @GetMapping
    @RequiresPermissions("user:list")
    public ResponseBo list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String username,
                           @RequestParam(required = false) String nickname) {
        if (page <= 0 || pageSize <= 0) {
            // page 小于等于 0 时返回全部用户（兼容非分页查询）
            List<User> list = userService.findAll();
            for (User u : list) {
                u.setPassword(null);
            }
            return ResponseBo.ok().putData(list);
        }
        PageResult<User> pageResult = userService.findPage(page, pageSize, username, nickname);
        for (User u : pageResult.getList()) {
            u.setPassword(null);
        }
        return ResponseBo.ok().putData(pageResult);
    }

    /**
     * 根据 ID 获取用户详情
     */
    @GetMapping("/{id}")
    @RequiresPermissions("user:list")
    public ResponseBo getById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return ResponseBo.ok().putData(user);
    }

    /**
     * 新增用户
     * 密码用 MD5 加密（盐值为用户名）
     */
    @PostMapping
    @RequiresPermissions("user:add")
    public ResponseBo add(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseBo.error("用户名已存在");
        }
        user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userService.add(user);
        return ResponseBo.ok("新增成功");
    }

    /**
     * 编辑用户信息
     * 密码为空时不修改密码
     */
    @PutMapping("/{id}")
    @RequiresPermissions("user:edit")
    public ResponseBo update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userService.update(user);
        return ResponseBo.ok("更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions("user:delete")
    public ResponseBo delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseBo.ok("删除成功");
    }

    /**
     * 分配用户角色
     */
    @PostMapping("/{id}/roles")
    @RequiresPermissions("user:assignRole")
    public ResponseBo assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        userService.assignRoles(id, body.get("roleIds"));
        return ResponseBo.ok("分配角色成功");
    }
}