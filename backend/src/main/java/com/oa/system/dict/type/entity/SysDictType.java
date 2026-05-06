package com.oa.system.dict.type.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典类型实体
 */
@Data
public class SysDictType implements Serializable {

    private Long id;
    private String dictType;
    private String dictName;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}