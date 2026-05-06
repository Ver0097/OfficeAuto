package com.oa.system.user.controller;

import com.oa.common.page.PageResult;
import com.oa.common.result.Result;
import com.oa.system.user.dto.*;
import com.oa.system.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<UserListVO>> list(UserQueryDTO queryDTO) {
        PageResult<UserListVO> result = userService.listUsers(queryDTO);
        return Result.success(result);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    public Result<UserDetailVO> detail(@PathVariable Long id) {
        UserDetailVO detail = userService.getUserDetail(id);
        return Result.success(detail);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserCreateDTO createDTO) {
        userService.createUser(createDTO);
        return Result.success();
    }

    /**
     * 编辑用户
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd/{id}")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        userService.resetPassword(id, password);
        return Result.success();
    }

    /**
     * 切换状态
     */
    @PutMapping("/status/{id}")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.changeStatus(id, status);
        return Result.success();
    }
}