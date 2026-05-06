package com.oa.system.user.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户列表响应
 */
@Data
public class UserListVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private String deptName;
    private Integer status;
    private LocalDateTime createTime;
}