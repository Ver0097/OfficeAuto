package com.oa.system.config.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改参数请求
 */
@Data
public class ConfigUpdateDTO {

    @NotNull(message = "参数ID不能为空")
    private Long id;

    @Size(max = 50, message = "参数键名最长50位")
    private String configKey;

    @Size(max = 500, message = "参数键值最长500位")
    private String configValue;

    @Size(max = 100, message = "参数名称最长100位")
    private String configName;

    private String configType;

    @Size(max = 200, message = "备注最长200位")
    private String remark;

    private Integer status;
}