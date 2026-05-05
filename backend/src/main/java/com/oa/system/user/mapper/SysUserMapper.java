package com.oa.system.user.mapper;

import com.oa.system.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper
 */
@Mapper
public interface SysUserMapper {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    SysUser selectById(@Param("id") Long id);

    /**
     * 插入用户
     *
     * @param user 用户实体
     * @return 影响行数
     */
    int insert(SysUser user);
}