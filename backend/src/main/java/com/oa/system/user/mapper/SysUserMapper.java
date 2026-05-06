package com.oa.system.user.mapper;

import com.oa.system.user.dto.*;
import com.oa.system.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserMapper {

    SysUser selectByUsername(@Param("username") String username);
    SysUser selectById(@Param("id") Long id);
    int insert(SysUser user);

    /**
     * 统计用户数量
     */
    Long countUsers(UserQueryDTO queryDTO);

    /**
     * 分页查询用户列表
     */
    List<UserListVO> selectUserList(UserQueryDTO queryDTO);

    /**
     * 查询用户详情
     */
    UserDetailVO selectUserDetailById(@Param("id") Long id);

    /**
     * 更新用户信息
     */
    int updateById(SysUser user);

    /**
     * 逻辑删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}