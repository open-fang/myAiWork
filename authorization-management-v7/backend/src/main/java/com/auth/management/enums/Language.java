package com.auth.management.enums;

import lombok.Getter;

/**
 * Language
 */
@Getter
public enum Language {

    ZH("ZH", "Chinese"),
    EN("EN", "English");

    private final String code;
    private final String name;

    Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.getCode().equals(code)) {
                return lang;
            }
        }
        return null;
    }
}