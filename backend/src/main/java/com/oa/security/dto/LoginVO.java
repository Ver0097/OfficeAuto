package com.oa.security.dto;

import lombok.Data;

/**
 * 登录响应 VO
 */
@Data
public class LoginVO {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
}