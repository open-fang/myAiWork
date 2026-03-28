package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Create Question Response (returns ID and code)
 */
@Data
public class CreateQuestionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String questionCode;
}