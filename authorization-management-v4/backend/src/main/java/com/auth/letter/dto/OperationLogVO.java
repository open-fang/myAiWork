package com.auth.letter.dto;

import lombok.Data;

@Data
public class OperationLogVO {
    private Long id;
    private String operation;
    private String operationText;
    private String operator;
    private String operatedAt;
}