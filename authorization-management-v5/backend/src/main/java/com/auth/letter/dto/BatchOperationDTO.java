package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Batch Operation DTO
 */
@Data
public class BatchOperationDTO {
    private List<Long> ids;
}