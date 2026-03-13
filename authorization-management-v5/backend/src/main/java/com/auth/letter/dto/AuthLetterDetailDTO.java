package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Authorization Letter Detail DTO (for create/update)
 */
@Data
public class AuthLetterDetailDTO {
    private String name;
    private List<String> authTargetLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String contentSummary;
    private List<AttachmentDTO> attachments;
    private List<SceneDTO> scenes;
}