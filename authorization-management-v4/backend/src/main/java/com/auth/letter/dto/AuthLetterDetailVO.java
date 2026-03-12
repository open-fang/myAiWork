package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

@Data
public class AuthLetterDetailVO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String statusText;
    private List<String> authPublishLevel;
    private String authPublishLevelText;
    private List<String> authPublishOrg;
    private String authPublishOrgText;
    private List<String> authTargetLevel;
    private String authTargetLevelText;
    private List<String> applicableRegion;
    private String applicableRegionText;
    private Integer publishYear;
    private String contentSummary;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
    private String publishedAt;
    private List<SceneVO> scenes;
    private List<AttachmentVO> attachments;
    private List<OperationLogVO> logs;
}