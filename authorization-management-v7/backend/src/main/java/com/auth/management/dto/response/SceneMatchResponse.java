package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Scene Match Response
 */
@Data
public class SceneMatchResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<MatchedScene> matchedScenes;

    @Data
    public static class MatchedScene implements Serializable {
        private Long sceneId;
        private String sceneName;
        private String decisionLevel;
        private String decisionLevelName;
    }
}