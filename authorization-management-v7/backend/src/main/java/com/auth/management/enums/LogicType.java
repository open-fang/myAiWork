package com.auth.management.enums;

import lombok.Getter;

/**
 * Logic Type (AND/OR)
 */
@Getter
public enum LogicType {

    AND("AND", "And"),
    OR("OR", "Or");

    private final String code;
    private final String name;

    LogicType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LogicType fromCode(String code) {
        for (LogicType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}