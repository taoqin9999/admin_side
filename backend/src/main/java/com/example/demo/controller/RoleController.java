package com.example.demo.controller;

import com.example.demo.pojo.ResponseBo;
import com.example.demo.pojo.Role;
import com.example.demo.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 * 提供角色列表查询、新增、编辑、删除、分配权限等功能
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表
     * 支持角色标识、描述模糊搜索
     */
    @GetMapping
    @RequiresPermissions("role:list")
    public ResponseBo list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String memo) {
        if (page <= 0 || pageSize <= 0) {
            return ResponseBo.ok().putData(roleService.findAll());
        }
        return ResponseBo.ok().putData(roleService.findPage(page, pageSize, name, memo));
    }

    /**
     * 根据 ID 获取角色详情
     */
    @GetMapping("/{id}")
    @RequiresPermissions("role:list")
    public ResponseBo getById(@PathVariable Long id) {
        return ResponseBo.ok().putData(roleService.findById(id));
    }

    /**
     * 新增角色
     */
    @PostMapping
    @RequiresPermissions("role:add")
    public ResponseBo add(@RequestBody Role role) {
        roleService.add(role);
        return ResponseBo.ok("新增成功");
    }

    /**
     * 编辑角色信息
     */
    @PutMapping("/{id}")
    @RequiresPermissions("role:edit")
    public ResponseBo update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.update(role);
        return ResponseBo.ok("更新成功");
    }

    /**
     * 删除角色（同时清除角色-权限关联）
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions("role:delete")
    public ResponseBo delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseBo.ok("删除成功");
    }

    /**
     * 分配角色权限
     */
    @PostMapping("/{id}/permissions")
    @RequiresPermissions("role:assignPerm")
    public ResponseBo assignPermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        roleService.assignPermissions(id, body.get("permissionIds"));
        return ResponseBo.ok("分配权限成功");
    }
}