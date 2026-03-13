package com.auth.letter.dto;

import lombok.Data;

/**
 * Scene Match Result VO
 */
@Data
public class SceneMatchResultVO {
    private Long sceneId;
    private String sceneName;
    private String decisionLevel;
    private String decisionLevelText;
}