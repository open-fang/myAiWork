package com.auth.letter.enums;

/**
 * Data Type Enum for rule parameters
 */
public enum DataType {
    TEXT("TEXT", "文本"),
    NUMBER("NUMBER", "数值"),
    FIELD("FIELD", "比对字段"),
    RATIO("RATIO", "比率");

    private final String code;
    private final String description;

    DataType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DataType fromCode(String code) {
        for (DataType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown data type code: " + code);
    }
}