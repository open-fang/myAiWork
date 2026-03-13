package com.auth.letter.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Scene Entity
 */
@Data
@Entity
@Table(name = "auth_scene")
public class AuthScene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth_letter_id", nullable = false)
    private Long authLetterId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "industry", length = 1000)
    private String industry;

    @Column(name = "business_scenario", length = 100)
    private String businessScenario;

    @Column(name = "decision_level", length = 50)
    private String decisionLevel;

    @Column(name = "rule_detail", columnDefinition = "TEXT")
    private String ruleDetail;

    @Column(name = "condition_groups", columnDefinition = "JSONB")
    @Type(type = "text")
    private String conditionGroups;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}