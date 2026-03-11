package com.auth.letter.entity;

import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import javax.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Authorization Letter Entity
 */
@Data
@Entity
@Table(name = "auth_letter")
public class AuthLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthLetterStatus status = AuthLetterStatus.DRAFT;

    /**
     * Publish level - old field, kept for compatibility
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "publish_level")
    private OrgLevel publishLevel;

    /**
     * Authorized level - old field, kept for compatibility
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "authorized_level")
    private OrgLevel authorizedLevel;

    /**
     * Authorization target level codes (JSON array from lookup)
     * 配置项编码: AUTH_TARGET_LEVEL
     */
    @Column(name = "auth_target_level", length = 500)
    private String authTargetLevel;

    /**
     * Applicable region codes (JSON array from lookup)
     * 配置项编码: APPLICABLE_REGION
     */
    @Column(name = "applicable_region", length = 500)
    private String applicableRegion;

    /**
     * Authorization publish level codes (JSON array from lookup)
     * 配置项编码: AUTH_PUBLISH_LEVEL
     */
    @Column(name = "auth_publish_level", length = 500)
    private String authPublishLevel;

    /**
     * Authorization publish organization codes (JSON array from lookup)
     * 配置项编码: AUTH_PUBLISH_ORG
     */
    @Column(name = "auth_publish_org", length = 1000)
    private String authPublishOrg;

    /**
     * Authorization letter publish year
     */
    @Column(name = "publish_year")
    private Integer publishYear;

    /**
     * Authorization letter content summary
     */
    @Column(name = "content_summary", columnDefinition = "TEXT")
    private String contentSummary;

    /**
     * Creator username
     */
    @Column(name = "created_by", length = 100)
    private String createdBy;

    /**
     * Updater username
     */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "authLetter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Scene> scenes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the letter is expired
     */
    public boolean isExpired() {
        if (validTo == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(validTo);
    }

    /**
     * Check if the letter is currently valid
     */
    public boolean isValid() {
        if (status != AuthLetterStatus.PUBLISHED) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validTo != null && now.isAfter(validTo)) {
            return false;
        }
        return true;
    }
}