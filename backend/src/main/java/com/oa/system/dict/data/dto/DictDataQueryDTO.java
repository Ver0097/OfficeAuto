package com.oa.system.dict.data.dto;

import com.oa.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataQueryDTO extends PageRequest {

    private String dictType;
    private String dictLabel;
    private Integer status;
}