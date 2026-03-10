package com.auth.letter.entity;

import com.auth.letter.enums.LogicOperator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Condition Entity - Supports nested conditions
 * Condition can be:
 * 1. A simple condition with field, operator, value
 * 2. A nested condition group with logic operator (AND/OR)
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
    @OrderBy("id ASC")
    private List<Condition> children = new ArrayList<>();

    /**
     * Logic operator for combining this condition with siblings
     */
    @Enumerated(EnumType.STRING)
    private LogicOperator logicOperator;

    /**
     * Field name for simple condition
     */
    @Column(name = "field_name", length = 100)
    private String fieldName;

    /**
     * Operator: EQ, NE, GT, GE, LT, LE, IN, LIKE, etc.
     */
    @Column(length = 20)
    private String operator;

    /**
     * Value for simple condition (can be JSON for IN operator)
     */
    @Column(length = 1000)
    private String value;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Check if this is a group condition (has children)
     */
    public boolean isGroup() {
        return children != null && !children.isEmpty();
    }

    /**
     * Check if this is a simple condition (no children)
     */
    public boolean isSimple() {
        return !isGroup();
    }
}