package com.auth.letter.dto;

import lombok.Data;

@Data
public class AttachmentVO {
    private Long id;
    private String docId;
    private String docName;
    private String docType;
    private Boolean isEncrypted;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}