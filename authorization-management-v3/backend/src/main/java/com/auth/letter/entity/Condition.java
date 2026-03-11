package com.auth.letter.entity;

import com.auth.letter.enums.LogicOperator;
import javax.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Condition Entity - Supports nested conditions
 */
@Data
@Entity
@Table(name = "condition")
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Condition parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Condition> children = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "logic_operator", length = 10)
    private LogicOperator logicOperator;

    @Column(name = "field_name", length = 100)
    private String fieldName;

    @Column(length = 20)
    private String operator;

    @Column(length = 1000)
    private String value;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}