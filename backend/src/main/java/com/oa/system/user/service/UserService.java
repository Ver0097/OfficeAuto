package com.oa.system.user.service;

import com.oa.common.page.PageResult;
import com.oa.system.user.dto.*;
import com.oa.system.user.entity.SysUser;

/**
 * 用户服务接口
 */
public interface UserService {

    SysUser getByUsername(String username);
    SysUser getById(Long id);

    /**
     * 分页查询用户列表
     */
    PageResult<UserListVO> listUsers(UserQueryDTO queryDTO);

    /**
     * 获取用户详情
     */
    UserDetailVO getUserDetail(Long id);

    /**
     * 创建用户
     */
    void createUser(UserCreateDTO createDTO);

    /**
     * 更新用户
     */
    void updateUser(UserUpdateDTO updateDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String password);

    /**
     * 切换状态
     */
    void changeStatus(Long id, Integer status);
}