package com.auth.management.enums;

import lombok.Getter;

/**
 * Authorization Letter Status
 */
@Getter
public enum AuthLetterStatus {

    DRAFT("DRAFT", "Draft"),
    PUBLISHED("PUBLISHED", "Published"),
    INVALID("INVALID", "Invalid");

    private final String code;
    private final String name;

    AuthLetterStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AuthLetterStatus fromCode(String code) {
        for (AuthLetterStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}