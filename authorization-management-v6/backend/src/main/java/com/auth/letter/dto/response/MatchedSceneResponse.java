package com.auth.letter.dto.response;

import java.io.Serializable;

/**
 * 匹配场景响应
 */
public class MatchedSceneResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sceneId;
    private String sceneName;
    private String decisionLevel;

    public MatchedSceneResponse() {
    }

    public MatchedSceneResponse(Long sceneId, String sceneName, String decisionLevel) {
        this.sceneId = sceneId;
        this.sceneName = sceneName;
        this.decisionLevel = decisionLevel;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getDecisionLevel() {
        return decisionLevel;
    }

    public void setDecisionLevel(String decisionLevel) {
        this.decisionLevel = decisionLevel;
    }
}