package com.auth.letter.dto;

import lombok.Data;

import java.util.List;

/**
 * 规则参数列表VO
 */
@Data
public class RuleParamListVO {
    private Long id;
    private String name;
    private String nameEn;
    private String status;
    private String dataType;
    private List<RuleParamDTO.BusinessMapping> businessMappings;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}