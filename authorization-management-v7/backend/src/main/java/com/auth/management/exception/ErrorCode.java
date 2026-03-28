package com.auth.management.exception;

import lombok.Getter;

/**
 * Error Code Definition
 */
@Getter
public enum ErrorCode {

    // Common Errors
    PARAM_ERROR(400, "Request parameter error"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error"),

    // Auth Letter Errors (1001-1003)
    AUTH_LETTER_NAME_EXISTS(1001, "Authorization letter name already exists"),
    AUTH_LETTER_STATUS_INVALID(1002, "Authorization letter status does not allow this operation"),
    AUTH_LETTER_INCOMPLETE(1003, "Authorization letter information incomplete, cannot publish"),

    // Scene Errors (1004-1005)
    SCENE_NAME_EXISTS(1004, "Scene name already exists"),
    SCENE_RULE_QUESTIONNAIRE_REQUIRED(1005, "Rule and questionnaire must be configured at least one"),

    // Rule Param Errors (1006)
    RULE_PARAM_NAME_EXISTS(1006, "Rule parameter name already exists"),

    // Questionnaire Errors (1007-1008)
    QUESTION_TEXT_LANGUAGE_DUPLICATE(1007, "Same language can only maintain one question text"),
    ANSWER_TEXT_LANGUAGE_DUPLICATE(1008, "Same language can only maintain one answer text");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}