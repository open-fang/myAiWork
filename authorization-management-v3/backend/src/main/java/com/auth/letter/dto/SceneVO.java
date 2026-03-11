package com.auth.letter.dto;

import lombok.Data;

import java.util.List;

/**
 * Scene View Object for returning scene details
 */
@Data
public class SceneVO {
    private Long id;
    private String sceneName;
    private List<String> industry;
    private String industryText;
    private String businessScenario;
    private String businessScenarioCode;
    private String decisionLevel;
    private String decisionLevelCode;
    private String ruleDetail;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
    private List<Object> conditions;
    private List<String> conditionLogics;
}