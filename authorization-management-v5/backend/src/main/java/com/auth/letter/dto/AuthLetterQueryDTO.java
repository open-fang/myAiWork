package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Authorization Letter Query DTO
 */
@Data
public class AuthLetterQueryDTO {
    private String name;
    private List<String> authTargetLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}