package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Authorization Letter Detail Response
 */
@Data
public class AuthLetterDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private List<String> authObjectLevel;
    private List<String> authObjectLevelNames;
    private List<String> applicableRegion;
    private List<String> applicableRegionNames;
    private List<String> authPublishLevel;
    private List<String> authPublishLevelNames;
    private List<String> authPublishOrg;
    private List<String> authPublishOrgNames;
    private Integer publishYear;
    private String summary;
    private String status;
    private String statusName;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}