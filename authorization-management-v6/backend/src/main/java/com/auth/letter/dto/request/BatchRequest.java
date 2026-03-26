package com.auth.letter.dto.request;

import java.io.Serializable;
import java.util.List;

/**
 * 批量操作请求
 */
public class BatchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> ids;
    private String operation;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}