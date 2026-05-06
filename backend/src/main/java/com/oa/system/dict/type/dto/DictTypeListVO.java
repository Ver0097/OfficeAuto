package com.oa.system.dict.type.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型列表响应
 */
@Data
public class DictTypeListVO {

    private Long id;
    private String dictType;
    private String dictName;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
}