package com.oa.security.controller;

import com.oa.common.exception.BusinessException;
import com.oa.common.result.Result;
import com.oa.common.result.ResultCode;
import com.oa.security.captcha.CaptchaService;
import com.oa.security.dto.*;
import com.oa.security.jwt.JwtUtils;
import com.oa.system.user.entity.SysUser;
import com.oa.system.user.service.UserService;
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