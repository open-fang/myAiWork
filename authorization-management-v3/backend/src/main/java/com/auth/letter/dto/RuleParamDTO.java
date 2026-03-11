package com.auth.letter.dto;

import lombok.Data;

import java.util.List;

/**
 * 规则参数创建/更新DTO
 */
@Data
public class RuleParamDTO {
    private Long id;
    private String name;
    private String nameEn;
    private String status;
    private String dataType;
    private List<BusinessMapping> businessMappings;

    @Data
    public static class BusinessMapping {
        private String businessObject;
        private String valueLogic;
    }
}