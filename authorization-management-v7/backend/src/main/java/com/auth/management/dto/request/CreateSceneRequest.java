package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Create Scene Request
 */
@Data
public class CreateSceneRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Scene name is required")
    @Size(max = 200, message = "Scene name must not exceed 200 characters")
    private String sceneName;

    @NotEmpty(message = "Industry is required")
    private List<String> industry;

    @NotBlank(message = "Business scene is required")
    private String businessScene;

    @NotBlank(message = "Decision level is required")
    private String decisionLevel;

    @Size(max = 1000, message = "Specific rule must not exceed 1000 characters")
    private String specificRule;

    private List<RuleGroupDTO> rules;

    private List<SceneQuestionnaireDTO> questionnaires;
}