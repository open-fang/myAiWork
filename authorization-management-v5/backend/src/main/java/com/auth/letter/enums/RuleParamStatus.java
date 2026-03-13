package com.auth.letter.enums;

/**
 * Rule Parameter Status Enum
 */
public enum RuleParamStatus {
    ACTIVE("ACTIVE", "生效"),
    INACTIVE("INACTIVE", "失效");

    private final String code;
    private final String description;

    RuleParamStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RuleParamStatus fromCode(String code) {
        for (RuleParamStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}