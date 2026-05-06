package com.oa.system.dict.type.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改字典类型请求
 */
@Data
public class DictTypeUpdateDTO {

    @NotNull(message = "字典类型ID不能为空")
    private Long id;

    @Size(max = 50, message = "字典类型编码最长50位")
    private String dictType;

    @Size(max = 100, message = "字典类型名称最长100位")
    private String dictName;

    @Size(max = 200, message = "备注最长200位")
    private String remark;

    private Integer status;
}