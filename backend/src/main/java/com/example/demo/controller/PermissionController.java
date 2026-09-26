package com.example.demo.controller;

import com.example.demo.pojo.Permission;
import com.example.demo.pojo.ResponseBo;
import com.example.demo.service.PermissionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 权限/菜单管理控制器
 * 提供权限列表查询、新增、编辑、删除、菜单树等功能
 */
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询权限列表
     * 支持权限标识、名称模糊搜索
     */
    @GetMapping
    @RequiresPermissions("permission:list")
    public ResponseBo list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String code,
                           @RequestParam(required = false) String name) {
        if (page <= 0 || pageSize <= 0) {
            return ResponseBo.ok().putData(permissionService.findAll());
        }
        return ResponseBo.ok().putData(permissionService.findPage(page, pageSize, code, name));
    }

    /**
     * 获取菜单树（供角色分配权限时使用）
     */
    @GetMapping("/tree")
    @RequiresPermissions("permission:list")
    public ResponseBo tree() {
        return ResponseBo.ok().putData(permissionService.findMenuTree());
    }

    /**
     * 根据 ID 获取权限详情
     */
    @GetMapping("/{id}")
    @RequiresPermissions("permission:list")
    public ResponseBo getById(@PathVariable Long id) {
        return ResponseBo.ok().putData(permissionService.findById(id));
    }

    /**
     * 新增权限/菜单
     */
    @PostMapping
    @RequiresPermissions("permission:add")
    public ResponseBo add(@RequestBody Permission permission) {
        if (permission.getPermType() == null) {
            permission.setPermType(2);
        }
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        permissionService.add(permission);
        return ResponseBo.ok("新增成功");
    }

    /**
     * 编辑权限信息
     */
    @PutMapping("/{id}")
    @RequiresPermissions("permission:edit")
    public ResponseBo update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.update(permission);
        return ResponseBo.ok("更新成功");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions("permission:delete")
    public ResponseBo delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseBo.ok("删除成功");
    }
}