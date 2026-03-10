package com.auth.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for match result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {

    /**
     * Whether match was successful
     */
    private boolean matched;

    /**
     * Matched scene name
     */
    private String sceneName;

    /**
     * Matched rule name
     */
    private String ruleName;

    /**
     * Decision level from matched scene
     */
    private Integer decisionLevel;

    /**
     * All matched scenes
     */
    private List<SceneMatch> matchedScenes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneMatch {
        private Long sceneId;
        private String sceneName;
        private Integer decisionLevel;
        private List<RuleMatch> matchedRules;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleMatch {
        private Long ruleId;
        private String ruleName;
    }
}