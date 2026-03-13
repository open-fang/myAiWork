package com.auth.letter.enums;

/**
 * Operation Type Enum for logging
 */
public enum OperationType {
    CREATE("CREATE", "创建"),
    UPDATE("UPDATE", "更新"),
    PUBLISH("PUBLISH", "发布"),
    EXPIRE("EXPIRE", "失效"),
    DELETE("DELETE", "删除");

    private final String code;
    private final String description;

    OperationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}