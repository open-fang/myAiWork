package com.auth.letter.enums;

public enum RuleParamStatus {
    ACTIVE("生效"),
    INACTIVE("失效");

    private final String description;

    RuleParamStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}