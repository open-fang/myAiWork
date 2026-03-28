package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Question List Response
 */
@Data
public class QuestionListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String questionCode;
    private String questionText;
    private String language;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}