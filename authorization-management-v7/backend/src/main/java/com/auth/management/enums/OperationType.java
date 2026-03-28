package com.auth.management.enums;

import lombok.Getter;

/**
 * Operation Type (Log Operation Type)
 */
@Getter
public enum OperationType {

    CREATE("CREATE", "Create"),
    UPDATE("UPDATE", "Update"),
    PUBLISH("PUBLISH", "Publish"),
    INVALIDATE("INVALIDATE", "Invalidate"),
    DELETE("DELETE", "Delete");

    private final String code;
    private final String name;

    OperationType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OperationType fromCode(String code) {
        for (OperationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}