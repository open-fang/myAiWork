package com.auth.letter.dto;

import lombok.Data;

/**
 * Attachment DTO (for create/update)
 */
@Data
public class AttachmentDTO {
    private Long id;
    private String docId;
    private String docName;
    private String docType;
    private Long fileSize;
    private Boolean encrypted;
}