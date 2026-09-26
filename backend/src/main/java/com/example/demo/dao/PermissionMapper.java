package com.example.demo.dao;

import com.example.demo.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
/**
 * 权限数据访问接口（MyBatis Mapper）
 */
@Mapper
public interface PermissionMapper {
    List<Permission> findByUsername(@Param("username") String username);

    List<Permission> findAll();

    List<Permission> findPage(@Param("offset") int offset, @Param("limit") int limit,
                              @Param("code") String code, @Param("name") String name);

    long countByCondition(@Param("code") String code, @Param("name") String name);

    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    Permission findById(@Param("id") Long id);

    int insert(Permission permission);

    int update(Permission permission);

    int deleteById(@Param("id") Long id);
}