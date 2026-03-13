package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Rule Parameter DTO (for create/update)
 */
@Data
public class RuleParamDTO {
    private Long id;
    private String name;
    private String nameEn;
    private String status;
    private String dataType;
    private String businessMappings; // JSON array of BusinessMapping
}