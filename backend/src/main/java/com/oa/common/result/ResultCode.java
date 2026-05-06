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
    USER_EXISTS(1005, "用户名已存在"),
    USER_NOT_FOUND(1006, "用户不存在"),
    PASSWORD_FORMAT_ERROR(1007, "密码格式错误"),
    CANNOT_DELETE_ADMIN(1008, "不能删除管理员账户");

    private final Integer code;
    private final String message;
}