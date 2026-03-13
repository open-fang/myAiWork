package com.auth.letter.enums;

/**
 * Authorization Letter Status Enum
 */
public enum AuthLetterStatus {
    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "已发布"),
    EXPIRED("EXPIRED", "已失效");

    private final String code;
    private final String description;

    AuthLetterStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AuthLetterStatus fromCode(String code) {
        for (AuthLetterStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}