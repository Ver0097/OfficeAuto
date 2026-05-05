package com.oa.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥（至少256位，HS256算法要求）
     */
    private String secret;

    /**
     * Token 有效期（毫秒）
     */
    private Long expiration;

    /**
     * Token 请求头名称
     */
    private String header;

    /**
     * Token 前缀
     */
    private String prefix;
}