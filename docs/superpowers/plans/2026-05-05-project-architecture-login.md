# OA管理系统 - 项目架构与用户登录模块 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 OA 管理系统基础架构，实现用户登录模块（含验证码、JWT 认证）

**Architecture:** 后端采用 Spring Boot 3 + Spring Security + JWT，前端采用 Vite + Vue3 + Element Plus。验证码存 Redis，Token 无状态认证。

**Tech Stack:** Spring Boot 3.2.x, Spring Security, JJWT 0.12.x, MyBatis 3.0.x, MySQL 8.x, Redis, Vue 3.4.x, Vite 5.x, Element Plus 2.x, Pinia 2.x, Axios 1.x

---

## 文件结构总览

```
OfficeAuto/
├── backend/
│   ├── pom.xml
│   ├── src/main/java/com/oa/
│   │   ├── OaApplication.java
│   │   ├── common/config/CorsConfig.java
│   │   ├── common/config/RedisConfig.java
│   │   ├── common/exception/BusinessException.java
│   │   ├── common/exception/GlobalExceptionHandler.java
│   │   ├── common/result/Result.java
│   │   ├── common/result/ResultCode.java
│   │   ├── security/config/SecurityConfig.java
│   │   ├── security/jwt/JwtProperties.java
│   │   ├── security/jwt/JwtUtils.java
│   │   ├── security/filter/JwtAuthenticationFilter.java
│   │   ├── security/captcha/CaptchaConfig.java
│   │   ├── security/captcha/CaptchaService.java
│   │   ├── security/controller/AuthController.java
│   │   ├── security/dto/LoginDTO.java
│   │   ├── security/dto/CaptchaVO.java
│   │   ├── security/dto/LoginVO.java
│   │   ├── security/dto/UserInfoVO.java
│   │   ├── system/user/entity/SysUser.java
│   │   ├── system/user/mapper/SysUserMapper.java
│   │   ├── system/user/service/UserService.java
│   │   ├── system/user/service/UserServiceImpl.java
│   │   ├── sql/schema.sql
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── mapper/SysUserMapper.xml
│
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   ├── src/main.js
│   ├── src/App.vue
│   ├── src/api/request.js
│   ├── src/api/auth.js
│   ├── src/utils/auth.js
│   ├── src/store/index.js
│   ├── src/store/user.js
│   ├── src/router/index.js
│   ├── src/views/login/index.vue
│   ├── src/views/home/index.vue
```

---

## 任务列表

- [√] Task 1: 创建后端项目基础结构
- [√] Task 2: 创建公共模块（Result、Exception）
- [√] Task 3: 创建 JWT 工具类
- [√] Task 4: 创建验证码模块
- [√] Task 5: 创建 Security 配置和过滤器
- [√] Task 6: 创建用户实体和 Mapper
- [√] Task 7: 创建认证控制器和登录接口
- [√] Task 8: 创建前端项目基础结构
- [ ] Task 9: 创建前端登录页面和路由
- [ ] Task 10: 创建数据库脚本和测试数据

---

## Task 1: 创建后端项目基础结构

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/oa/OaApplication.java`
- Create: `backend/src/main/resources/application.yml`

- [ ] **Step 1: 创建 backend 目录结构**

```bash
mkdir -p backend/src/main/java/com/oa/{common/{config,exception,result},security/{config,jwt,filter,captcha,controller,dto},system/user/{entity,mapper,service}}
mkdir -p backend/src/main/resources/mapper
mkdir -p backend/src/test/java/com/oa
mkdir -p backend/sql
```

- [ ] **Step 2: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.oa</groupId>
    <artifactId>oa-backend</artifactId>
    <version>1.0.0</version>
    <name>oa-backend</name>
    <description>OA管理系统后端</description>

    <properties>
        <java.version>17</java.version>
        <jjwt.version>0.12.5</jjwt.version>
        <mybatis.version>3.0.3</mybatis.version>
        <kaptcha.version>2.3.2</kaptcha.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- Spring Boot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Spring Boot Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.version}</version>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Kaptcha 验证码 -->
        <dependency>
            <groupId>com.github.penggle</groupId>
            <artifactId>kaptcha</artifactId>
            <version>${kaptcha.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: 创建启动类 OaApplication.java**

```java
package com.oa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OA管理系统启动类
 */
@SpringBootApplication
public class OaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaApplication.class, args);
    }
}
```

- [ ] **Step 4: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  application:
    name: oa-backend

  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/oa_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root

  # Redis 配置
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0
      timeout: 10000ms

# MyBatis 配置
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.oa.system.user.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# JWT 配置
jwt:
  secret: oa-system-secret-key-2024-must-be-at-least-256-bits
  expiration: 86400000  # 24小时（毫秒）
  header: Authorization
  prefix: "Bearer "

# 验证码配置
captcha:
  expiration: 300  # 5分钟（秒）
```

- [ ] **Step 5: 提交**

```bash
git add backend/
git commit -m "feat: 初始化后端项目结构"
```

---

## Task 2: 创建公共模块（Result、Exception）

**Files:**
- Create: `backend/src/main/java/com/oa/common/result/Result.java`
- Create: `backend/src/main/java/com/oa/common/result/ResultCode.java`
- Create: `backend/src/main/java/com/oa/common/exception/BusinessException.java`
- Create: `backend/src/main/java/com/oa/common/exception/GlobalExceptionHandler.java`
- Create: `backend/src/main/java/com/oa/common/config/CorsConfig.java`
- Create: `backend/src/main/java/com/oa/common/config/RedisConfig.java`

- [ ] **Step 1: 创建 Result.java（统一响应封装）**

```java
package com.oa.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应（使用错误码）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应（使用错误码和自定义消息）
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }
}
```

- [ ] **Step 2: 创建 ResultCode.java（响应码枚举）**

```java
package com.oa.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 服务端错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    
    // 业务错误 1xxx
    CAPTCHA_ERROR(1001, "验证码错误"),
    CAPTCHA_EXPIRED(1002, "验证码已过期"),
    LOGIN_ERROR(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "账户已被禁用"),
    USER_EXISTS(1005, "用户名已存在");

    private final Integer code;
    private final String message;
}
```

- [ ] **Step 3: 创建 BusinessException.java（业务异常）**

```java
package com.oa.common.exception;

import com.oa.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;
    private final String message;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_ERROR.getCode();
        this.message = message;
    }
}
```

- [ ] **Step 4: 创建 GlobalExceptionHandler.java（全局异常处理）**

```java
package com.oa.common.exception;

import com.oa.common.result.Result;
import com.oa.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null 
            ? e.getBindingResult().getFieldError().getDefaultMessage() 
            : "参数错误";
        log.error("参数校验异常: {}", message);
        return Result.error(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null 
            ? e.getBindingResult().getFieldError().getDefaultMessage() 
            : "参数错误";
        log.error("参数绑定异常: {}", message);
        return Result.error(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ResultCode.INTERNAL_ERROR);
    }
}
```

- [ ] **Step 5: 创建 CorsConfig.java（跨域配置）**

```java
package com.oa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源（生产环境应配置具体域名）
        config.addAllowedOriginPattern("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许携带凭证（Cookie等）
        config.setAllowCredentials(true);
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);
        // 暴露的响应头
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 6: 创建 RedisConfig.java（Redis配置）**

```java
package com.oa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // Key 使用 String 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value 使用 String 序列化（验证码等简单数据）
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add backend/src/main/java/com/oa/common/
git commit -m "feat: 添加公共模块（统一响应、异常处理、跨域、Redis配置）"
```

---

## Task 3: 创建 JWT 工具类

**Files:**
- Create: `backend/src/main/java/com/oa/security/jwt/JwtProperties.java`
- Create: `backend/src/main/java/com/oa/security/jwt/JwtUtils.java`

- [ ] **Step 1: 创建 JwtProperties.java（JWT配置属性）**

```java
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
     * JWT 密钥（至少256位）
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
```

- [ ] **Step 2: 创建 JwtUtils.java（JWT工具类）**

```java
package com.oa.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析 Token 获取 Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("userId", Long.class);
        }
        return null;
    }

    /**
     * 从 Token 获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return false;
            }
            // 检查是否过期
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取完整的 Token 头（含前缀）
     *
     * @param token JWT Token
     * @return 完整的 Authorization 头值
     */
    public String getFullToken(String token) {
        return jwtProperties.getPrefix() + token;
    }

    /**
     * 从 Authorization 头提取 Token
     *
     * @param authHeader Authorization 头值
     * @return JWT Token（不含前缀）
     */
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(jwtProperties.getPrefix())) {
            return authHeader.substring(jwtProperties.getPrefix().length());
        }
        return null;
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add backend/src/main/java/com/oa/security/jwt/
git commit -m "feat: 添加 JWT 配置和工具类"
```

---

## Task 4: 创建验证码模块

**Files:**
- Create: `backend/src/main/java/com/oa/security/captcha/CaptchaConfig.java`
- Create: `backend/src/main/java/com/oa/security/captcha/CaptchaService.java`
- Create: `backend/src/main/java/com/oa/security/dto/CaptchaVO.java`

- [ ] **Step 1: 创建 CaptchaConfig.java（验证码配置）**

```java
package com.oa.security.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置
 */
@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha captchaProducer() {
        Properties properties = new Properties();
        // 图片边框
        properties.setProperty("kaptcha.border", "no");
        // 图片宽度
        properties.setProperty("kaptcha.image.width", "120");
        // 图片高度
        properties.setProperty("kaptcha.image.height", "40");
        // 验证码字符数
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 验证码字符范围
        properties.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHJKLMNPQRSTUVWXYZ23456789");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 字体样式
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        // 干扰线颜色
        properties.setProperty("kaptcha.noise.color", "gray");
        // 背景颜色渐变
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");

        Config config = new Config(properties);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
```

- [ ] **Step 2: 创建 CaptchaVO.java（验证码响应DTO）**

```java
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
     * 验证码图片（Base64）
     */
    private String captchaImage;
}
```

- [ ] **Step 3: 创建 CaptchaService.java（验证码服务）**

```java
package com.oa.security.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.oa.common.exception.BusinessException;
import com.oa.common.result.ResultCode;
import com.oa.security.dto.CaptchaVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final DefaultKaptcha captchaProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 验证码缓存 Key 前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码过期时间（秒）
     */
    @Value("${captcha.expiration:300}")
    private Integer captchaExpiration;

    /**
     * 生成验证码
     *
     * @return 验证码响应 VO
     */
    public CaptchaVO generateCaptcha() {
        // 生成唯一标识
        String captchaKey = UUID.randomUUID().toString();
        
        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        
        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);
        
        // 将图片转为 Base64
        String captchaImage = imageToBase64(image);
        
        // 将验证码存入 Redis
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(redisKey, captchaText, captchaExpiration, TimeUnit.SECONDS);
        
        log.info("生成验证码: key={}, text={}", captchaKey, captchaText);
        
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaKey(captchaKey);
        captchaVO.setCaptchaImage(captchaImage);
        return captchaVO;
    }

    /**
     * 验证验证码
     *
     * @param captchaKey   验证码唯一标识
     * @param captchaCode  用户输入的验证码
     * @return 是否验证成功
     */
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }
        
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        Object storedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            throw new BusinessException(ResultCode.CAPTCHA_EXPIRED);
        }
        
        // 删除已使用的验证码（一次性）
        redisTemplate.delete(redisKey);
        
        // 验证码忽略大小写
        return storedCode.toString().equalsIgnoreCase(captchaCode);
    }

    /**
     * 图片转 Base64
     *
     * @param image 图片
     * @return Base64 字符串
     */
    private String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("图片转 Base64 失败", e);
            throw new BusinessException("验证码生成失败");
        }
    }
}
```

- [ ] **Step 4: 提交**

```bash
git add backend/src/main/java/com/oa/security/captcha/
git add backend/src/main/java/com/oa/security/dto/
git commit -m "feat: 添加验证码模块（配置、服务、DTO）"
```

---

## Task 5: 创建 Security 配置和过滤器

**Files:**
- Create: `backend/src/main/java/com/oa/security/filter/JwtAuthenticationFilter.java`
- Create: `backend/src/main/java/com/oa/security/config/SecurityConfig.java`

- [ ] **Step 1: 创建 JwtAuthenticationFilter.java（JWT认证过滤器）**

```java
package com.oa.security.filter;

import com.oa.security.jwt.JwtProperties;
import com.oa.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 获取 Authorization 头
        String authHeader = request.getHeader(jwtProperties.getHeader());
        
        // 提取 Token
        String token = jwtUtils.extractToken(authHeader);
        
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            // 解析 Token 获取用户信息
            Claims claims = jwtUtils.parseToken(token);
            if (claims != null) {
                Long userId = claims.get("userId", Long.class);
                String username = claims.getSubject();
                
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userId,  // principal 存用户ID
                        null,    // credentials
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                
                // 设置到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("JWT 认证成功: userId={}, username={}", userId, username);
            }
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}
```

- [ ] **Step 2: 创建 SecurityConfig.java（Security配置）**

```java
package com.oa.security.config;

import com.oa.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（无状态 JWT 不需要）
            .csrf(AbstractHttpConfigurer::disable)
            
            // 无状态 Session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 请求授权配置
            .authorizeHttpRequests(auth -> auth
                // 公开路径（无需认证）
                .requestMatchers(
                    "/api/auth/captcha",
                    "/api/auth/login"
                ).permitAll()
                
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            
            // 添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add backend/src/main/java/com/oa/security/filter/
git add backend/src/main/java/com/oa/security/config/
git commit -m "feat: 添加 Security 配置和 JWT 认证过滤器"
```

---

## Task 6: 创建用户实体和 Mapper

**Files:**
- Create: `backend/src/main/java/com/oa/system/user/entity/SysUser.java`
- Create: `backend/src/main/java/com/oa/system/user/mapper/SysUserMapper.java`
- Create: `backend/src/main/java/com/oa/system/user/service/UserService.java`
- Create: `backend/src/main/java/com/oa/system/user/service/UserServiceImpl.java`
- Create: `backend/src/main/resources/mapper/SysUserMapper.xml`

- [ ] **Step 1: 创建 SysUser.java（用户实体）**

```java
package com.oa.system.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class SysUser implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0正常 1删除
     */
    private Integer deleted;
}
```

- [ ] **Step 2: 创建 SysUserMapper.java（用户Mapper接口）**

```java
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
```

- [ ] **Step 3: 创建 SysUserMapper.xml（MyBatis XML）**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.oa.system.user.mapper.SysUserMapper">

    <resultMap id="SysUserResultMap" type="com.oa.system.user.entity.SysUser">
        <id column="id" property="id"/>
        <result column="username" property="username"/>
        <result column="password" property="password"/>
        <result column="real_name" property="realName"/>
        <result column="email" property="email"/>
        <result column="phone" property="phone"/>
        <result column="dept_id" property="deptId"/>
        <result column="status" property="status"/>
        <result column="avatar" property="avatar"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
        <result column="deleted" property="deleted"/>
    </resultMap>

    <!-- 根据用户名查询用户 -->
    <select id="selectByUsername" resultMap="SysUserResultMap">
        SELECT * FROM sys_user 
        WHERE username = #{username} AND deleted = 0
    </select>

    <!-- 根据ID查询用户 -->
    <select id="selectById" resultMap="SysUserResultMap">
        SELECT * FROM sys_user 
        WHERE id = #{id} AND deleted = 0
    </select>

    <!-- 插入用户 -->
    <insert id="insert" parameterType="com.oa.system.user.entity.SysUser">
        INSERT INTO sys_user (
            username, password, real_name, email, phone, 
            dept_id, status, avatar
        ) VALUES (
            #{username}, #{password}, #{realName}, #{email}, #{phone},
            #{deptId}, #{status}, #{avatar}
        )
    </insert>

</mapper>
```

- [ ] **Step 4: 创建 UserService.java（用户服务接口）**

```java
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
```

- [ ] **Step 5: 创建 UserServiceImpl.java（用户服务实现）**

```java
package com.oa.system.user.service;

import com.oa.system.user.entity.SysUser;
import com.oa.system.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }
}
```

- [ ] **Step 6: 提交**

```bash
git add backend/src/main/java/com/oa/system/user/
git add backend/src/main/resources/mapper/
git commit -m "feat: 添加用户实体、Mapper和Service"
```

---

## Task 7: 创建认证控制器和登录接口

**Files:**
- Create: `backend/src/main/java/com/oa/security/dto/LoginDTO.java`
- Create: `backend/src/main/java/com/oa/security/dto/LoginVO.java`
- Create: `backend/src/main/java/com/oa/security/dto/UserInfoVO.java`
- Create: `backend/src/main/java/com/oa/security/controller/AuthController.java`

- [ ] **Step 1: 创建 LoginDTO.java（登录请求DTO）**

```java
package com.oa.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

    /**
     * 验证码Key
     */
    @NotBlank(message = "验证码Key不能为空")
    private String captchaKey;
}
```

- [ ] **Step 2: 创建 LoginVO.java（登录响应VO）**

```java
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
```

- [ ] **Step 3: 创建 UserInfoVO.java（用户信息VO）**

```java
package com.oa.security.dto;

import lombok.Data;

/**
 * 用户信息 VO
 */
@Data
public class UserInfoVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 角色列表
     */
    private String[] roles;
}
```

- [ ] **Step 4: 创建 AuthController.java（认证控制器）**

```java
package com.oa.security.controller;

import com.oa.common.exception.BusinessException;
import com.oa.common.result.Result;
import com.oa.common.result.ResultCode;
import com.oa.security.captcha.CaptchaService;
import com.oa.security.dto.*;
import com.oa.security.jwt.JwtUtils;
import com.oa.system.user.entity.SysUser;
import com.oa.system.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CaptchaService captchaService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        CaptchaVO captcha = captchaService.generateCaptcha();
        return Result.success(captcha);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 1. 验证验证码
        if (!captchaService.validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptchaCode())) {
            throw new BusinessException(ResultCode.CAPTCHA_ERROR);
        }

        // 2. 查询用户
        SysUser user = userService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 5. 生成 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 6. 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setRoles(new String[]{"user"}); // 后续从角色表获取
        loginVO.setUserInfo(userInfo);

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

        return Result.success(loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user/info")
    public Result<UserInfoVO> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setRoles(new String[]{"user"}); // 后续从角色表获取

        return Result.success(userInfo);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态，登出只需前端删除 Token
        // 后续可加入 Token 黑名单机制
        return Result.success();
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add backend/src/main/java/com/oa/security/
git commit -m "feat: 添加认证控制器和登录接口"
```

---

## Task 8: 创建前端项目基础结构

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/api/request.js`
- Create: `frontend/src/utils/auth.js`

- [ ] **Step 1: 创建 frontend 目录结构**

```bash
mkdir -p frontend/src/{api,utils,store,router,views/{login,home}}
mkdir -p frontend/public
```

- [ ] **Step 2: 创建 package.json**

```json
{
  "name": "oa-frontend",
  "version": "1.0.0",
  "description": "OA管理系统前端",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.21",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.7",
    "axios": "^1.6.8",
    "element-plus": "^2.6.1"
  },
  "devDependencies": {
    "vite": "^5.2.0",
    "@vitejs/plugin-vue": "^5.0.4"
  }
}
```

- [ ] **Step 3: 创建 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 4: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OA管理系统</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 5: 创建 main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
```

- [ ] **Step 6: 创建 App.vue**

```vue
<template>
  <router-view />
</template>

<script setup>
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}
</style>
```

- [ ] **Step 7: 创建 utils/auth.js（Token工具）**

```javascript
const TOKEN_KEY = 'oa_token'
const USER_INFO_KEY = 'oa_user_info'

/**
 * 获取 Token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置 Token
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除 Token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  const userInfo = localStorage.getItem(USER_INFO_KEY)
  return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 设置用户信息
 */
export function setUserInfo(userInfo) {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
}

/**
 * 判断是否已登录
 */
export function isLoggedIn() {
  return !!getToken()
}
```

- [ ] **Step 8: 创建 api/request.js（Axios封装）**

```javascript
import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加 Token
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    // 业务成功
    if (res.code === 200) {
      return res
    }
    // 业务失败
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    const status = error.response?.status
    if (status === 401) {
      // Token 过期，清除登录状态，跳转到登录页
      removeToken()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else if (status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 9: 安装依赖并提交**

```bash
cd frontend
npm install
git add frontend/
git commit -m "feat: 初始化前端项目结构（Vite + Vue3 + Element Plus）"
```

---

## Task 9: 创建前端登录页面和路由

**Files:**
- Create: `frontend/src/api/auth.js`
- Create: `frontend/src/store/index.js`
- Create: `frontend/src/store/user.js`
- Create: `frontend/src/router/index.js`
- Create: `frontend/src/views/login/index.vue`
- Create: `frontend/src/views/home/index.vue`

- [ ] **Step 1: 创建 api/auth.js（认证API）**

```javascript
import request from './request'

/**
 * 获取验证码
 */
export function getCaptcha() {
  return request.get('/auth/captcha')
}

/**
 * 登录
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 登出
 */
export function logout() {
  return request.post('/auth/logout')
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request.get('/auth/user/info')
}
```

- [ ] **Step 2: 创建 store/index.js（Pinia入口）**

```javascript
import { createPinia } from 'pinia'

const pinia = createPinia()

export default pinia
```

- [ ] **Step 3: 创建 store/user.js（用户状态）**

```javascript
import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, setUserInfo, getUserInfo } from '@/utils/auth'
import { login, logout, getUserInfo as fetchUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUserInfo() || null
  }),

  actions: {
    /**
     * 登录
     */
    async login(loginData) {
      const res = await login(loginData)
      this.token = res.data.token
      this.userInfo = res.data.userInfo
      setToken(this.token)
      setUserInfo(this.userInfo)
      return res
    },

    /**
     * 登出
     */
    async logout() {
      try {
        await logout()
      } catch (e) {
        // 忽略登出接口错误
      }
      this.token = ''
      this.userInfo = null
      removeToken()
    },

    /**
     * 刷新用户信息
     */
    async refreshUserInfo() {
      const res = await fetchUserInfo()
      this.userInfo = res.data
      setUserInfo(this.userInfo)
    }
  }
})
```

- [ ] **Step 4: 创建 router/index.js（路由配置）**

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const loggedIn = isLoggedIn()
  
  if (to.meta.requiresAuth && !loggedIn) {
    // 需要认证但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && loggedIn) {
    // 已登录访问登录页，跳转到首页
    next('/')
  } else {
    next()
  }
})

export default router
```

- [ ] **Step 5: 创建 views/home/index.vue（首页占位）**

```vue
<template>
  <div class="home-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <span class="title">OA管理系统</span>
          <div class="user-info">
            <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
            <el-button type="text" @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <el-card>
          <h2>欢迎登录OA管理系统</h2>
          <p>当前用户：{{ userStore.userInfo?.username }}</p>
          <p>Token有效，系统运行正常。</p>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
}

.el-header {
  background-color: #409EFF;
  color: white;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.title {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-main {
  background-color: #f5f5f5;
}
</style>
```

- [ ] **Step 6: 创建 views/login/index.vue（登录页面）**

```vue
<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">OA管理系统</h2>
      
      <el-form ref="formRef" :model="loginForm" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="captchaCode">
          <div class="captcha-row">
            <el-input
              v-model="loginForm.captchaCode"
              placeholder="验证码"
              prefix-icon="Picture"
              size="large"
              style="width: 200px"
            />
            <img
              :src="captchaImage"
              class="captcha-image"
              @click="refreshCaptcha"
              title="点击刷新验证码"
            />
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCaptcha } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  captchaCode: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaKey.value = res.data.captchaKey
    captchaImage.value = res.data.captchaImage
  } catch (e) {
    ElMessage.error('获取验证码失败')
  }
}

// 登录
const handleLogin = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  
  loading.value = true
  
  try {
    await userStore.login({
      username: loginForm.username,
      password: loginForm.password,
      captchaCode: loginForm.captchaCode,
      captchaKey: captchaKey.value
    })
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 登录失败，刷新验证码
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 400px;
  padding: 20px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.captcha-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-image {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>
```

- [ ] **Step 7: 提交**

```bash
git add frontend/src/
git commit -m "feat: 添加前端登录页面、路由守卫和用户状态管理"
```

---

## Task 10: 创建数据库脚本和测试数据

**Files:**
- Create: `backend/sql/schema.sql`

- [ ] **Step 1: 创建 sql/schema.sql（数据库建表脚本）**

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS oa_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE oa_system;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password        VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name       VARCHAR(50) COMMENT '真实姓名',
    email           VARCHAR(100) COMMENT '邮箱',
    phone           VARCHAR(20) COMMENT '手机号',
    dept_id         BIGINT COMMENT '部门ID（后续关联）',
    status          TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    avatar          VARCHAR(255) COMMENT '头像URL',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',

    INDEX idx_username (username),
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入初始管理员数据（密码为 BCrypt 加密的 "123456"）
INSERT INTO sys_user (username, password, real_name, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7', '系统管理员', 1);
```

- [ ] **Step 2: 执行数据库脚本**

```bash
# 连接 MySQL 执行脚本
mysql -u root -p < backend/sql/schema.sql

# 或在 MySQL 客户端中执行
# mysql> source backend/sql/schema.sql
```

- [ ] **Step 3: 提交**

```bash
git add backend/sql/
git commit -m "feat: 添加数据库建表脚本和初始管理员数据"
```

---

## 验收清单

完成所有任务后，执行以下验收测试：

### 后端验收

- [ ] **启动后端服务**

```bash
cd backend
mvn spring-boot:run
```

预期：服务正常启动，端口 8080

- [ ] **测试验证码接口**

```bash
curl http://localhost:8080/api/auth/captcha
```

预期：返回 `{ "code": 200, "data": { "captchaKey": "...", "captchaImage": "data:image/png;base64,..."} }`

- [ ] **测试登录接口**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456","captchaCode":"从验证码获取","captchaKey":"从验证码获取"}'
```

预期：返回 `{ "code": 200, "data": { "token": "...", "userInfo": {...} } }`

- [ ] **测试用户信息接口**

```bash
curl http://localhost:8080/api/auth/user/info \
  -H "Authorization: Bearer <登录返回的token>"
```

预期：返回用户信息

### 前端验收

- [ ] **启动前端服务**

```bash
cd frontend
npm run dev
```

预期：服务正常启动，端口 3000

- [ ] **浏览器访问**

访问 http://localhost:3000

预期：
1. 显示登录页面
2. 验证码正常显示
3. 使用 admin/123456 登录成功
4. 跳转到首页，显示用户信息
5. 点击"退出"返回登录页

---

<!-- END_OF_PLAN -->