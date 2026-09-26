package com.example.demo.service;

import com.example.demo.dao.SysConfigMapper;
import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
/**
 * 系统参数业务逻辑层
 */
@Service
public class SysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    public List<SysConfig> findAll() {
        return sysConfigMapper.findAll();
    }

    public PageResult<SysConfig> findPage(int page, int pageSize, String paramKey) {
        int offset = (page - 1) * pageSize;
        List<SysConfig> list = sysConfigMapper.findPage(offset, pageSize, paramKey);
        long total = sysConfigMapper.countByCondition(paramKey);
        return new PageResult<>(list, total, page, pageSize);
    }

    public SysConfig findById(Long id) {
        return sysConfigMapper.findById(id);
    }

    /**
     * 更新系统参数值，先校验正则
     */
    public void update(SysConfig sysConfig) {
        SysConfig existing = sysConfigMapper.findById(sysConfig.getId());
        if (existing == null) {
            throw new RuntimeException("系统参数不存在");
        }
        // 如果有正则校验规则，校验新值
        if (existing.getRegex() != null && !existing.getRegex().isEmpty()) {
            boolean matches = Pattern.matches(existing.getRegex(), sysConfig.getParamValue());
            if (!matches) {
                throw new RuntimeException("参数值不符合校验规则: " + existing.getRegex());
            }
        }
        sysConfigMapper.update(sysConfig);
    }
}