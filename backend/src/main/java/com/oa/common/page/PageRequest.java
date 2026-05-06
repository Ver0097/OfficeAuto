package com.oa.common.page;

import lombok.Data;

/**
 * 分页请求基类
 */
@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private String orderType = "asc";

    /**
     * 计算 offset 值
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}