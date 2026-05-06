package com.oa.system.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增参数请求
 */
@Data
public class ConfigCreateDTO {

    @NotBlank(message = "参数键名不能为空")
    @Size(max = 50, message = "参数键名最长50位")
    private String configKey;

    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值最长500位")
    private String configValue;

    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称最长100位")
    private String configName;

    private String configType;

    @Size(max = 200, message = "备注最长200位")
    private String remark;

    private Integer status;
}