package com.auth.letter.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RuleParam {
    private Long id;
    private String name;
    private String nameEn;
    private String status;
    private String dataType;
    private String businessMappings;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}