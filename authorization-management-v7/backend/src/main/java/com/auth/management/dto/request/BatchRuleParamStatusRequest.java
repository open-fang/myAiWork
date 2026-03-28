package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * Batch Status Update Request for Rule Parameter
 */
@Data
public class BatchRuleParamStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Rule parameter IDs are required")
    private List<Long> ids;

    @NotBlank(message = "Status is required")
    private String status;
}