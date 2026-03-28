package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Authorization Letter List Response
 */
@Data
public class AuthLetterListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String status;
    private String statusName;
    private String authObjectLevel;
    private String applicableRegion;
    private String authPublishLevel;
    private String authPublishOrg;
    private Integer publishYear;
    private String createdBy;
    private String createdByName;
    private LocalDateTime createdTime;
}