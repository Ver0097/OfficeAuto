package com.oa.system.dict.data.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据列表响应
 */
@Data
public class DictDataListVO {

    private Long id;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    private Integer dictSort;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
}