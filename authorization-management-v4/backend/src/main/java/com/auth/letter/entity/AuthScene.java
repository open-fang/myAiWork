package com.auth.letter.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuthScene {
    private Long id;
    private Long authLetterId;
    private String sceneName;
    private String industry;
    private String businessScenario;
    private String decisionLevel;
    private String ruleDetail;
    private String conditionGroups;
    private Integer orderIndex;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}