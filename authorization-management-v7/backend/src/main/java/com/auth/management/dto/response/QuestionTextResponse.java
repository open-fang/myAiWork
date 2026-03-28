package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Question Text Response
 */
@Data
public class QuestionTextResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String questionText;
    private String language;
    private String createdBy;
    private LocalDateTime createdTime;
}