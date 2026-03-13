package com.auth.letter.dto;

import lombok.Data;

/**
 * Authorization Letter List VO
 */
@Data
public class AuthLetterListVO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String statusText;
    private String authTargetLevel;
    private String authTargetLevelText;
    private String applicableRegion;
    private String applicableRegionText;
    private String authPublishLevel;
    private String authPublishLevelText;
    private String authPublishOrg;
    private String authPublishOrgText;
    private Integer publishYear;
    private String createdBy;
    private String createdAt;
}