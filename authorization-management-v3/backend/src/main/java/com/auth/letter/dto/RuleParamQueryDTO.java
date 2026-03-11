package com.auth.letter.dto;

import lombok.Data;

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