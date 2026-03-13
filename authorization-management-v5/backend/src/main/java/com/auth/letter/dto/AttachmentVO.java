package com.auth.letter.dto;

import lombok.Data;

/**
 * Attachment VO (for response)
 */
@Data
public class AttachmentVO {
    private Long id;
    private String docId;
    private String docName;
    private String docType;
    private String docTypeText;
    private Long fileSize;
    private Boolean encrypted;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}