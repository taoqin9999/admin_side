package com.example.demo.service;

import com.example.demo.dao.SysLogMapper;
import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统操作日志业务逻辑层（只读）
 */
@Service
public class SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    /**
     * 分页查询操作日志（支持用户名、操作描述、时间范围筛选）
     */
    public PageResult<SysLog> findPage(int page, int pageSize, String username, String operation,
                                       String startTime, String endTime) {
        int offset = (page - 1) * pageSize;
        List<SysLog> list = sysLogMapper.findPage(offset, pageSize, username, operation, startTime, endTime);
        long total = sysLogMapper.countByCondition(username, operation, startTime, endTime);
        return new PageResult<>(list, total, page, pageSize);
    }

    /**
     * 导出：查询全部匹配条件的操作日志（不分页）
     */
    public List<SysLog> findAllForExport(String username, String operation, String startTime, String endTime) {
        return sysLogMapper.findForExport(username, operation, startTime, endTime);
    }
}
