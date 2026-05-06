package com.oa.system.user.dto;

import com.oa.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageRequest {
    private String username;
    private String realName;
    private Integer status;
    private Long deptId;
}