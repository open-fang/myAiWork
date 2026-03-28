package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Answer Response
 */
@Data
public class AnswerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String answerCode;
    private List<AnswerTextResponse> answerTexts;
    private Integer sortOrder;
}