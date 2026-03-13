package com.auth.letter.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Attachment Entity
 */
@Data
@Entity
@Table(name = "auth_attachment")
public class AuthAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth_letter_id", nullable = false)
    private Long authLetterId;

    @Column(name = "doc_id", nullable = false, length = 100)
    private String docId;

    @Column(name = "doc_name", nullable = false, length = 500)
    private String docName;

    @Column(name = "doc_type", length = 50)
    private String docType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "encrypted")
    private Boolean encrypted;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (encrypted == null) {
            encrypted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}