package com.auth.management.enums;

import lombok.Getter;

/**
 * Data Type
 */
@Getter
public enum DataType {

    TEXT("TEXT", "Text"),
    NUMBER("NUMBER", "Number"),
    FIELD("FIELD", "Compare Field"),
    RATIO("RATIO", "Ratio");

    private final String code;
    private final String name;

    DataType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DataType fromCode(String code) {
        for (DataType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}