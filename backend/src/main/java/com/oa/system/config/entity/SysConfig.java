package com.oa.system.config.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统参数实体
 */
@Data
public class SysConfig implements Serializable {

    private Long id;
    private String configKey;
    private String configValue;
    private String configName;
    private String configType;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}