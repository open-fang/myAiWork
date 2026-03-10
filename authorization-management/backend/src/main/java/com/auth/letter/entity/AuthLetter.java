package com.auth.letter.entity;

import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import jakarta.persistence.*;
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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthLetterStatus status = AuthLetterStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "publish_level")
    private OrgLevel publishLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "authorized_level")
    private OrgLevel authorizedLevel;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

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