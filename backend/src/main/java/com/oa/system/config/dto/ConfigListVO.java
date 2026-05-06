package com.oa.system.config.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 参数列表响应
 */
@Data
public class ConfigListVO {

    private Long id;
    private String configKey;
    private String configValue;
    private String configName;
    private String configType;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
}