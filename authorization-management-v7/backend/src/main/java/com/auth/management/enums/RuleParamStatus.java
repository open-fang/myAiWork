package com.auth.management.enums;

import lombok.Getter;

/**
 * Rule Parameter Status
 */
@Getter
public enum RuleParamStatus {

    ACTIVE("ACTIVE", "Active"),
    INACTIVE("INACTIVE", "Inactive");

    private final String code;
    private final String name;

    RuleParamStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RuleParamStatus fromCode(String code) {
        for (RuleParamStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}