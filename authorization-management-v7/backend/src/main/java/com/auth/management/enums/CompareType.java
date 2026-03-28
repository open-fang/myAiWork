package com.auth.management.enums;

import lombok.Getter;

/**
 * Compare Type (Value Comparison Type)
 */
@Getter
public enum CompareType {

    FIELD("FIELD", "Compare Field"),
    NUMBER("NUMBER", "Number"),
    TEXT("TEXT", "Text"),
    RATIO("RATIO", "Ratio");

    private final String code;
    private final String description;

    CompareType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CompareType fromCode(String code) {
        for (CompareType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}