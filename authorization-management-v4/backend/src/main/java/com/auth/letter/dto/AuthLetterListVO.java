package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

@Data
public class AuthLetterListVO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String statusText;
    private List<String> authTargetLevel;
    private String authTargetLevelText;
    private List<String> applicableRegion;
    private String applicableRegionText;
    private List<String> authPublishLevel;
    private String authPublishLevelText;
    private List<String> authPublishOrg;
    private String authPublishOrgText;
    private Integer publishYear;
    private String createdBy;
    private String createdAt;
}