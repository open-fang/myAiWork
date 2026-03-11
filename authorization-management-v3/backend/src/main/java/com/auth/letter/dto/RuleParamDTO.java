package com.auth.letter.dto;

import lombok.Data;

import java.util.List;

/**
 * 规则参数查询DTO
 */
@Data
public class RuleParamQueryDTO {
    private String name;
    private String nameEn;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

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