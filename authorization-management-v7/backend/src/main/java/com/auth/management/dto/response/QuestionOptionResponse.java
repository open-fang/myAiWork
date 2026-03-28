package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Question Option Response (for dropdown)
 */
@Data
public class QuestionOptionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long questionId;
    private String questionCode;
    private String questionText;
    private List<AnswerOptionResponse> answers;

    @Data
    public static class AnswerOptionResponse implements Serializable {
        private Long answerId;
        private String answerCode;
        private String answerText;
    }
}