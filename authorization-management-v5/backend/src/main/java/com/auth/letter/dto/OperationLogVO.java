package com.auth.letter.dto;

import lombok.Data;

/**
 * Operation Log VO
 */
@Data
public class OperationLogVO {
    private Long id;
    private String operationType;
    private String operationTypeText;
    private String operator;
    private String operatedAt;
}