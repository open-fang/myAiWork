package com.auth.letter.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuthLetter {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String authTargetLevel;
    private String applicableRegion;
    private String authPublishLevel;
    private String authPublishOrg;
    private Integer publishYear;
    private String contentSummary;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}