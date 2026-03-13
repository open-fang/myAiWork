package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Rule Parameter Query DTO
 */
@Data
public class RuleParamQueryDTO {
    private String name;
    private String nameEn;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}