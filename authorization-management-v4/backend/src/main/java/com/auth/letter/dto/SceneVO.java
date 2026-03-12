package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

@Data
public class SceneVO {
    private Long id;
    private String sceneName;
    private List<String> industry;
    private String industryText;
    private String businessScenario;
    private String decisionLevel;
    private String ruleDetail;
    private Object conditionGroups;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}