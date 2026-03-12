package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchOperationDTO {
    private List<Long> ids;
}