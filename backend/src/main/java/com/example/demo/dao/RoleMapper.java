package com.example.demo.dao;

import com.example.demo.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
/**
 * 角色数据访问接口（MyBatis Mapper）
 */
@Mapper
public interface RoleMapper {
    List<Role> findByUsername(@Param("username") String username);

    List<Role> findAll();

    List<Role> findPage(@Param("offset") int offset, @Param("limit") int limit,
                        @Param("name") String name, @Param("memo") String memo);

    long countByCondition(@Param("name") String name, @Param("memo") String memo);

    Role findById(@Param("id") Long id);

    int insert(Role role);

    int update(Role role);

    int deleteById(@Param("id") Long id);

    int deleteRolePermission(@Param("roleId") Long roleId);

    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    List<Role> findByUserId(@Param("userId") Long userId);
}