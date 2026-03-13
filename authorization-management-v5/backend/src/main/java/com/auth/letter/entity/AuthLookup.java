package com.auth.letter.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Lookup Entity for dropdown options
 */
@Data
@Entity
@Table(name = "auth_lookup")
public class AuthLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lookup_type", nullable = false, length = 50)
    private String lookupType;

    @Column(name = "lookup_code", nullable = false, length = 100)
    private String lookupCode;

    @Column(name = "lookup_name", nullable = false, length = 200)
    private String lookupName;

    @Column(name = "parent_code", length = 100)
    private String parentCode;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
        if (status == null) {
            status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}