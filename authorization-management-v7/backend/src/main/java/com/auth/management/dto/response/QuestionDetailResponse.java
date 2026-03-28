package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Question Detail Response
 */
@Data
public class QuestionDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String questionCode;
    private List<QuestionTextResponse> questionTexts;
    private List<AnswerResponse> answers;
}