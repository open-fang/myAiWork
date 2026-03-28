package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Question Text DTO
 */
@Data
public class QuestionTextDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Question text is required")
    @Size(max = 500, message = "Question text must not exceed 500 characters")
    private String questionText;

    @NotBlank(message = "Language is required")
    private String language;
}