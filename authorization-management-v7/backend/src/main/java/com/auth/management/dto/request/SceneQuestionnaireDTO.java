package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Scene Questionnaire DTO
 */
@Data
public class SceneQuestionnaireDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    private Long correctAnswerId;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;
}