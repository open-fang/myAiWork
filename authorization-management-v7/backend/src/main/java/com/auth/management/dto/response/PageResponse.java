package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Pagination Response Structure
 */
@Data
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> list;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;

    public PageResponse() {
    }

    public PageResponse(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageResponse<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        return new PageResponse<>(list, total, pageNum, pageSize);
    }
}