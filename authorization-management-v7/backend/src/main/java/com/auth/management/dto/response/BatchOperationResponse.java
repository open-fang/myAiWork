package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Batch Operation Response
 */
@Data
public class BatchOperationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer successCount;
    private Integer failCount;
    private List<FailDetail> failDetails;

    @Data
    public static class FailDetail implements Serializable {
        private Long id;
        private String reason;
    }
}