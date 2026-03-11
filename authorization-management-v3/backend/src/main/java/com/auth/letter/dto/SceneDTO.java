package com.auth.letter.dto;

import lombok.Data;

import java.util.List;

/**
 * Scene DTO for creating/updating scenes
 */
@Data
public class SceneDTO {
    private Long id;
    private String name;
    private List<String> industry;
    private String businessScenario;
    private String decisionLevel;
    private String ruleDetail;
    private List<ConditionGroupDTO> conditions;
}

/**
 * Condition Group DTO for nested conditions
 */
@Data
class ConditionGroupDTO {
    private String type; // "condition" or "group"
    private String logic; // "AND" or "OR" - for groups
    private String field; // for conditions
    private String operator;
    private String compareType;
    private String compareField;
    private String compareValue;
    private String unit;
    private List<ConditionGroupDTO> conditions; // for groups
    private List<String> conditionLogics; // for groups
}