package com.oa.system.user.service;

import com.oa.system.user.entity.SysUser;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getByUsername(String username);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    SysUser getById(Long id);
}