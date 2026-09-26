package com.example.demo.controller;

import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.ResponseBo;
import com.example.demo.pojo.SysConfig;
import com.example.demo.service.SysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统参数管理控制器
 * 提供系统参数列表查询和值修改功能
 */
@RestController
@RequestMapping("/api/sysConfig")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 分页查询系统参数列表
     * 支持参数键模糊搜索
     */
    @GetMapping
    @RequiresPermissions("sysConfig:list")
    public ResponseBo list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String paramKey) {
        if (page <= 0 || pageSize <= 0) {
            return ResponseBo.ok().putData(sysConfigService.findAll());
        }
        PageResult<SysConfig> pageResult = sysConfigService.findPage(page, pageSize, paramKey);
        return ResponseBo.ok().putData(pageResult);
    }

    /**
     * 根据 ID 获取系统参数详情
     */
    @GetMapping("/{id}")
    @RequiresPermissions("sysConfig:list")
    public ResponseBo getById(@PathVariable Long id) {
        SysConfig config = sysConfigService.findById(id);
        return ResponseBo.ok().putData(config);
    }

    /**
     * 修改系统参数值
     * 后端会根据该参数预设的 regex 校验新值是否合法
     */
    @PutMapping("/{id}")
    @RequiresPermissions("sysConfig:edit")
    public ResponseBo update(@PathVariable Long id, @RequestBody SysConfig sysConfig) {
        sysConfig.setId(id);
        try {
            sysConfigService.update(sysConfig);
            return ResponseBo.ok("更新成功");
        } catch (RuntimeException e) {
            return ResponseBo.error(e.getMessage());
        }
    }
}