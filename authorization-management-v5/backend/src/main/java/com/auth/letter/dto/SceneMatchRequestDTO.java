package com.auth.letter.dto;

import lombok.Data;
import java.util.Map;

/**
 * Scene Match Request DTO
 */
@Data
public class SceneMatchRequestDTO {
    private Long authLetterId;
    private Map<String, Object> params; // Rule field values
}