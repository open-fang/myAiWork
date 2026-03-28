package com.auth.management.enums;

import lombok.Getter;

/**
 * Operator Type (Comparison Operator)
 */
@Getter
public enum OperatorType {

    GT("GT", ">", "Greater Than"),
    LT("LT", "<", "Less Than"),
    EQ("EQ", "=", "Equal"),
    GE("GE", ">=", "Greater Than or Equal"),
    LE("LE", "<=", "Less Than or Equal"),
    NE("NE", "!=", "Not Equal");

    private final String code;
    private final String symbol;
    private final String description;

    OperatorType(String code, String symbol, String description) {
        this.code = code;
        this.symbol = symbol;
        this.description = description;
    }

    public static OperatorType fromCode(String code) {
        for (OperatorType operator : values()) {
            if (operator.getCode().equals(code)) {
                return operator;
            }
        }
        return null;
    }
}