package com.example.demo.dao;

import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
/**
 * 用户数据访问接口（MyBatis Mapper）
 */
@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Long id);

    List<User> findAll();

    List<User> findPage(@Param("offset") int offset, @Param("limit") int limit,
                        @Param("username") String username, @Param("nickname") String nickname);

    long countByCondition(@Param("username") String username, @Param("nickname") String nickname);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);

    int deleteUserRole(@Param("userId") Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}