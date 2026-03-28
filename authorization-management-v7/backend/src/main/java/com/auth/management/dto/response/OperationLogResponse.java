package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Operation Log Response
 */
@Data
public class OperationLogResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String operation;
    private String operationName;
    private String operator;
    private String operatorName;
    private LocalDateTime operationTime;
}