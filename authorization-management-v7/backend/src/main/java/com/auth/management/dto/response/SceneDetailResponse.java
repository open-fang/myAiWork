package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Scene Detail Response
 */
@Data
public class SceneDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sceneName;
    private List<String> industry;
    private String businessScene;
    private String decisionLevel;
    private String specificRule;
    private List<RuleGroupResponse> rules;
    private List<SceneQuestionnaireResponse> questionnaires;
}