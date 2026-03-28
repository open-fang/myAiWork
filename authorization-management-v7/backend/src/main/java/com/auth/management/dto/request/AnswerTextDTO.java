package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Answer Text DTO
 */
@Data
public class AnswerTextDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Answer text is required")
    @Size(max = 200, message = "Answer text must not exceed 200 characters")
    private String answerText;

    @NotBlank(message = "Language is required")
    private String language;
}