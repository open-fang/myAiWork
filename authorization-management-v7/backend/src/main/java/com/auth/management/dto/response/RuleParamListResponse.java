package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Rule Parameter List Response
 */
@Data
public class RuleParamListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String nameEn;
    private List<BusinessObjectDTO> businessObjects;
    private String status;
    private String statusName;
    private String dataType;
    private String dataTypeName;
    private Long referenceFieldId;
    private String referenceFieldName;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;

    @Data
    public static class BusinessObjectDTO implements Serializable {
        private String businessObject;
        private String valueLogic;
    }
}