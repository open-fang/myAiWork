package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Scene Questionnaire Response
 */
@Data
public class SceneQuestionnaireResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long questionId;
    private String questionCode;
    private String questionText;
    private Long correctAnswerId;
    private String correctAnswerCode;
    private String correctAnswerText;
    private Integer sortOrder;
}