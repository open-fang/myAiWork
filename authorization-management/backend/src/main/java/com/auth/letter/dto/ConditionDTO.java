package com.auth.letter.dto;

import com.auth.letter.enums.LogicOperator;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Condition - Supports nested structure
 */
@Data
public class ConditionDTO {

    private Long id;

    private Long ruleId;

    private Long parentId;

    private LogicOperator logicOperator;

    // Simple condition fields
    private String fieldName;

    private String operator;

    private String value;

    // Nested conditions
    private List<ConditionDTO> children;

    private LocalDateTime createdAt;
}