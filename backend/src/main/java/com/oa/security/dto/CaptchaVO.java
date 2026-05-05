package com.oa.security.dto;

import lombok.Data;

/**
 * 验证码响应 VO
 */
@Data
public class CaptchaVO {

    /**
     * 验证码唯一标识（用于验证）
     */
    private String captchaKey;

    /**
     * 验证码图片（Base64格式）
     */
    private String captchaImage;
}