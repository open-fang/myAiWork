package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

@Data
public class SceneDTO {
    private Long id;
    private String sceneName;
    private List<String> industry;
    private String businessScenario;
    private String decisionLevel;
    private String ruleDetail;
    private Object conditionGroups;
}