package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Authorization Letter Detail VO (for response)
 */
@Data
public class AuthLetterDetailVO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String statusText;
    private List<String> authTargetLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String contentSummary;
    private List<AttachmentVO> attachments;
    private List<SceneVO> scenes;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
    private String publishedAt;
}