package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * Batch Delete Request
 */
@Data
public class BatchDeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "IDs are required")
    private List<Long> ids;
}