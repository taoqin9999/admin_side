package com.example.demo.dao;

import com.example.demo.pojo.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统操作日志数据访问接口（MyBatis Mapper）
 */
@Mapper
public interface SysLogMapper {

    /**
     * 保存系统操作日志
     */
    int save(SysLog sysLog);

    /**
     * 分页查询操作日志（支持用户名、操作描述、时间范围筛选）
     */
    List<SysLog> findPage(@Param("offset") int offset, @Param("limit") int limit,
                          @Param("username") String username, @Param("operation") String operation,
                          @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 查询操作日志总数（支持用户名、操作描述、时间范围筛选）
     */
    long countByCondition(@Param("username") String username, @Param("operation") String operation,
                          @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 导出：查询全部匹配条件的操作日志（不分页）
     */
    List<SysLog> findForExport(@Param("username") String username, @Param("operation") String operation,
                               @Param("startTime") String startTime, @Param("endTime") String endTime);
}
