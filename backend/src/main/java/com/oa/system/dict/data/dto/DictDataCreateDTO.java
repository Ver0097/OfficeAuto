package com.oa.system.dict.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增字典数据请求
 */
@Data
public class DictDataCreateDTO {

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 50, message = "字典类型最长50位")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签最长100位")
    private String dictLabel;

    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值最长100位")
    private String dictValue;

    private Integer dictSort;

    @Size(max = 200, message = "备注最长200位")
    private String remark;

    private Integer status;
}