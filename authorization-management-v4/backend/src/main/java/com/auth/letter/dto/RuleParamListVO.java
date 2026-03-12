package com.auth.letter.dto;

import lombok.Data;

@Data
public class RuleParamListVO {
    private Long id;
    private String name;
    private String nameEn;
    private String status;
    private String statusText;
    private String dataType;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}