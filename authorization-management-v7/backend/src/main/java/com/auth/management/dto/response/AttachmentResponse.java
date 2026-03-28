package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Attachment Response
 */
@Data
public class AttachmentResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String docId;
    private String docName;
    private String docType;
    private String docTypeName;
    private Integer isEncrypted;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}