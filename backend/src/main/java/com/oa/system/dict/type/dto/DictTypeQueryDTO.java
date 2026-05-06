package com.oa.system.dict.type.dto;

import com.oa.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeQueryDTO extends PageRequest {

    private String dictType;
    private String dictName;
    private Integer status;
}