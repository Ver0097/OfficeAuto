package com.oa.system.config.dto;

import com.oa.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigQueryDTO extends PageRequest {

    private String configKey;
    private String configName;
    private Integer status;
}