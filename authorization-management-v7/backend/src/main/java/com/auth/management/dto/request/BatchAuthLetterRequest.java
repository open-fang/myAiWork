package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Batch Operation Request for Authorization Letter
 */
@Data
public class BatchAuthLetterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Authorization letter IDs are required")
    private List<Long> ids;

    @NotNull(message = "Operation type is required")
    private String operation; // PUBLISH, INVALIDATE, DELETE
}