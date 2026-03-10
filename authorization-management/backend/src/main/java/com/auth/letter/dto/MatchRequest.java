package com.auth.letter.dto;

import com.auth.letter.enums.OrgLevel;
import lombok.Data;

import java.util.Map;

/**
 * Request DTO for matching scenes
 */
@Data
public class MatchRequest {

    /**
     * Authorization letter ID
     */
    private Long authLetterId;

    /**
     * Requester's organization level
     */
    private OrgLevel requesterLevel;

    /**
     * Dynamic field values passed by caller system
     */
    private Map<String, Object> fields;
}