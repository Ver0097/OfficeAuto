package com.oa.system.user.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户详情响应
 */
@Data
public class UserDetailVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private String deptName;
    private Integer status;
    private String avatar;
    private List<Long> roleIds;
}