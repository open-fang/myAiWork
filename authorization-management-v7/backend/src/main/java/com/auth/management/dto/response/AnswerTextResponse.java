package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Answer Text Response
 */
@Data
public class AnswerTextResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String answerText;
    private String language;
}