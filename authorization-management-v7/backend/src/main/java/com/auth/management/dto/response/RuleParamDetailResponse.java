package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Rule Parameter Detail Response
 */
@Data
public class RuleParamDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String nameEn;
    private List<RuleParamListResponse.BusinessObjectDTO> businessObjects;
    private String status;
    private String dataType;
    private Long referenceFieldId;
    private String createdBy;
    private LocalDateTime createdTime;
}