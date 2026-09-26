package com.example.demo.dao;

import com.example.demo.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
/**
 * 系统参数数据访问接口（MyBatis Mapper）
 */
@Mapper
public interface SysConfigMapper {
    List<SysConfig> findAll();

    List<SysConfig> findPage(@Param("offset") int offset, @Param("limit") int limit,
                             @Param("paramKey") String paramKey);

    long countByCondition(@Param("paramKey") String paramKey);

    SysConfig findById(@Param("id") Long id);

    int update(SysConfig sysConfig);
}