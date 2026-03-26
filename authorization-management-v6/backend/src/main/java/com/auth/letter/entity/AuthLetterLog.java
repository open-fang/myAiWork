package com.auth.letter.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 操作日志实体类
 */
public class AuthLetterLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long authLetterId;
    private String operation;
    private String operator;
    private Timestamp operationTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthLetterId() {
        return authLetterId;
    }

    public void setAuthLetterId(Long authLetterId) {
        this.authLetterId = authLetterId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Timestamp getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Timestamp operationTime) {
        this.operationTime = operationTime;
    }
}