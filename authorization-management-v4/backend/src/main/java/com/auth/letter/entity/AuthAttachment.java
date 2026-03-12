package com.auth.letter.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuthAttachment {
    private Long id;
    private Long authLetterId;
    private String docId;
    private String docName;
    private String docType;
    private Boolean isEncrypted;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}