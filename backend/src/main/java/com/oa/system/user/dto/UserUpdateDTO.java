package com.oa.system.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 编辑用户请求
 */
@Data
public class UserUpdateDTO {
    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private List<Long> roleIds;
}