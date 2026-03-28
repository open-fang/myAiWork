package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Answer DTO
 */
@Data
public class AnswerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Answer texts are required")
    private List<AnswerTextDTO> answerTexts;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;
}