package com.auth.letter.service;

import com.auth.letter.entity.Condition;
import com.auth.letter.enums.LogicOperator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Condition Evaluator - Evaluates conditions including nested conditions
 */
@Component
public class ConditionEvaluator {

    /**
     * Evaluate a list of conditions against provided fields
     */
    public boolean evaluate(List<Condition> conditions, Map<String, Object> fields) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        if (conditions.size() == 1) {
            return evaluateCondition(conditions.get(0), fields);
        }

        // Multiple conditions - use logic operator
        LogicOperator operator = conditions.get(0).getLogicOperator();
        if (operator == null) {
            operator = LogicOperator.AND; // Default to AND
        }

        boolean result = evaluateCondition(conditions.get(0), fields);
        for (int i = 1; i < conditions.size(); i++) {
            boolean nextResult = evaluateCondition(conditions.get(i), fields);
            if (operator == LogicOperator.AND) {
                result = result && nextResult;
            } else {
                result = result || nextResult;
            }
        }

        return result;
    }

    /**
     * Evaluate a single condition
     */
    private boolean evaluateCondition(Condition condition, Map<String, Object> fields) {
        if (condition.isGroup()) {
            // Nested condition group
            return evaluate(condition.getChildren(), fields);
        }

        // Simple condition
        return evaluateSimpleCondition(condition, fields);
    }

    /**
     * Evaluate a simple condition
     */
    private boolean evaluateSimpleCondition(Condition condition, Map<String, Object> fields) {
        String fieldName = condition.getFieldName();
        String operator = condition.getOperator();
        String expectedValue = condition.getValue();

        if (fieldName == null || operator == null) {
            return true;
        }

        Object actualValue = fields.get(fieldName);
        if (actualValue == null) {
            return "IS_NULL".equals(operator) || "EQ".equals(operator) && expectedValue == null;
        }

        return switch (operator) {
            case "EQ" -> equals(actualValue, expectedValue);
            case "NE" -> !equals(actualValue, expectedValue);
            case "GT" -> compare(actualValue, expectedValue) > 0;
            case "GE" -> compare(actualValue, expectedValue) >= 0;
            case "LT" -> compare(actualValue, expectedValue) < 0;
            case "LE" -> compare(actualValue, expectedValue) <= 0;
            case "LIKE" -> actualValue.toString().contains(expectedValue);
            case "IN" -> isInList(actualValue, expectedValue);
            case "NOT_IN" -> !isInList(actualValue, expectedValue);
            case "IS_NULL" -> actualValue == null;
            case "IS_NOT_NULL" -> actualValue != null;
            default -> false;
        };
    }

    private boolean equals(Object actual, String expected) {
        if (actual == null && expected == null) return true;
        if (actual == null || expected == null) return false;
        return actual.toString().equals(expected);
    }

    private int compare(Object actual, String expected) {
        if (actual instanceof Number && expected != null) {
            BigDecimal actualNum = new BigDecimal(actual.toString());
            BigDecimal expectedNum = new BigDecimal(expected);
            return actualNum.compareTo(expectedNum);
        }
        if (actual instanceof Comparable && expected != null) {
            @SuppressWarnings("unchecked")
            Comparable<Object> comparable = (Comparable<Object>) actual;
            return comparable.compareTo(expected);
        }
        return actual.toString().compareTo(expected);
    }

    private boolean isInList(Object actual, String expectedList) {
        if (expectedList == null || expectedList.isEmpty()) {
            return false;
        }
        // Expected format: "value1,value2,value3"
        String[] values = expectedList.split(",");
        String actualStr = actual.toString();
        for (String value : values) {
            if (value.trim().equals(actualStr)) {
                return true;
            }
        }
        return false;
    }
}