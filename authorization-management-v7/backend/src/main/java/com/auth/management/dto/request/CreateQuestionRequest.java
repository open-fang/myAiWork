package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Create Questionnaire Question Request
 */
@Data
public class CreateQuestionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Question texts are required")
    private List<QuestionTextDTO> questionTexts;

    @NotEmpty(message = "Answers are required")
    private List<AnswerDTO> answers;
}