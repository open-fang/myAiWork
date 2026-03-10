package com.auth.letter.enums;

/**
 * Logic Operator for Conditions
 */
public enum LogicOperator {
    AND("与"),
    OR("或");

    private final String description;

    LogicOperator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}