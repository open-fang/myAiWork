package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Scene DTO (for create/update)
 */
@Data
public class SceneDTO {
    private Long id;
    private String name;
    private List<String> industry;
    private String businessScenario;
    private String decisionLevel;
    private String ruleDetail;
    private String conditionGroups; // JSON string
}