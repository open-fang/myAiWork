package com.auth.letter.enums;

/**
 * Authorization Letter Status
 */
public enum AuthLetterStatus {
    DRAFT("草稿"),
    PUBLISHED("已发布"),
    EXPIRED("已失效");

    private final String description;

    AuthLetterStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}