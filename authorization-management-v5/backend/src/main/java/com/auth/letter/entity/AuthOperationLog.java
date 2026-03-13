package com.auth.letter.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Operation Log Entity
 */
@Data
@Entity
@Table(name = "auth_operation_log")
public class AuthOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth_letter_id", nullable = false)
    private Long authLetterId;

    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    @Column(name = "operation_desc", length = 500)
    private String operationDesc;

    @Column(name = "operator", length = 100)
    private String operator;

    @Column(name = "operated_at", nullable = false)
    private LocalDateTime operatedAt;

    @PrePersist
    protected void onCreate() {
        if (operatedAt == null) {
            operatedAt = LocalDateTime.now();
        }
    }
}