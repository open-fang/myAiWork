package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Scene VO (for response)
 */
@Data
public class SceneVO {
    private Long id;
    private String name;
    private List<String> industry;
    private String industryText;
    private String businessScenario;
    private String businessScenarioText;
    private String decisionLevel;
    private String decisionLevelText;
    private String ruleDetail;
    private String conditionGroups;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}