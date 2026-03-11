package com.auth.letter.dto;

import lombok.Data;

import java.util.List;

/**
 * Authorization Letter Detail View Object
 * For returning authorization letter details
 */
@Data
public class AuthLetterDetailVO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String statusText;
    private List<String> authPublishLevel;
    private List<String> authPublishLevelText;
    private List<String> authPublishOrg;
    private List<String> authPublishOrgText;
    private List<String> authTargetLevel;
    private List<String> authTargetLevelText;
    private List<String> applicableRegion;
    private List<String> applicableRegionText;
    private Integer publishYear;
    private String contentSummary;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
    private String publishedAt;
    private List<SceneVO> scenes;
}