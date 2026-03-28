package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Rule Parameter Option Response (for dropdown)
 */
@Data
public class RuleParamOptionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private String dataType;
}