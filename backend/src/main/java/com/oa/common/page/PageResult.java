package com.oa.common.page;

import lombok.Data;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageResult<T> {
    private Long total;
    private Integer pages;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;

    public PageResult(Long total, Integer pageNum, Integer pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
}