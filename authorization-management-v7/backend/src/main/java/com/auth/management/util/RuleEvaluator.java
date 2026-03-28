package com.auth.management.util;

import com.auth.management.dto.response.RuleConditionResponse;
import com.auth.management.dto.response.RuleGroupResponse;
import com.auth.management.dto.response.SceneDetailResponse;
import com.auth.management.enums.CompareType;
import com.auth.management.enums.LogicType;
import com.auth.management.enums.OperatorType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Rule Evaluator - Evaluates rule conditions against input data
 * Supports nested condition groups with AND/OR logic
 */
@Component
public class RuleEvaluator {

    /**
     * Evaluate if a scene matches the input data
     * @param scene the scene with rules
     * @param data the input data map
     * @return true if the scene matches
     */
    public boolean evaluateScene(SceneDetailResponse scene, Map<String, Object> data) {
        List<RuleGroupResponse> rules = scene.getRules();

        // If no rules configured, consider it as matched
        if (rules == null || rules.isEmpty()) {
            return true;
        }

        // All rule groups must match (AND logic between rule groups)
        for (RuleGroupResponse ruleGroup : rules) {
            if (!evaluateRuleGroup(ruleGroup, data)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Evaluate a rule group
     */
    public boolean evaluateRuleGroup(RuleGroupResponse ruleGroup, Map<String, Object> data) {
        List<RuleConditionResponse> conditions = ruleGroup.getConditions();

        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        boolean result = true;
        String prevLogic = LogicType.AND.getCode();

        for (int i = 0; i < conditions.size(); i++) {
            RuleConditionResponse condition = conditions.get(i);

            boolean currentResult;
            if (Boolean.TRUE.equals(condition.getIsGroup())) {
                // Recursive evaluation for nested condition group
                currentResult = evaluateConditionGroup(condition, data);
            } else {
                // Single condition evaluation
                currentResult = evaluateCondition(condition, data);
            }

            // Apply logic type from previous condition (first condition's logic is ignored)
            if (i == 0) {
                result = currentResult;
            } else {
                if (LogicType.OR.getCode().equals(prevLogic)) {
                    result = result || currentResult;
                } else {
                    result = result && currentResult;
                }
            }

            // Store current condition's logic type for next iteration
            prevLogic = condition.getLogicType();
        }

        return result;
    }

    /**
     * Evaluate a nested condition group
     */
    private boolean evaluateConditionGroup(RuleConditionResponse groupCondition, Map<String, Object> data) {
        List<RuleConditionResponse> children = groupCondition.getChildren();

        if (children == null || children.isEmpty()) {
            return true;
        }

        String groupLogic = groupCondition.getGroupLogicType();
        if (groupLogic == null) {
            groupLogic = LogicType.AND.getCode();
        }

        if (LogicType.OR.getCode().equals(groupLogic)) {
            // OR logic: return true if any child matches
            for (RuleConditionResponse child : children) {
                boolean childResult;
                if (Boolean.TRUE.equals(child.getIsGroup())) {
                    childResult = evaluateConditionGroup(child, data);
                } else {
                    childResult = evaluateCondition(child, data);
                }
                if (childResult) {
                    return true;
                }
            }
            return false;
        } else {
            // AND logic: return true only if all children match
            for (RuleConditionResponse child : children) {
                boolean childResult;
                if (Boolean.TRUE.equals(child.getIsGroup())) {
                    childResult = evaluateConditionGroup(child, data);
                } else {
                    childResult = evaluateCondition(child, data);
                }
                if (!childResult) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Evaluate a single condition
     */
    private boolean evaluateCondition(RuleConditionResponse condition, Map<String, Object> data) {
        String fieldCode = condition.getFieldCode();
        String operator = condition.getOperator();
        String compareType = condition.getCompareType();
        String compareValue = condition.getCompareValue();
        String compareFieldCode = condition.getCompareFieldCode();

        // Get field value from data
        Object fieldValue = getFieldValue(fieldCode, data);
        if (fieldValue == null) {
            return false;
        }

        // Get compare value based on compare type
        Object compareTarget;
        switch (compareType) {
            case "FIELD":
                compareTarget = getFieldValue(compareFieldCode, data);
                break;
            case "NUMBER":
                compareTarget = parseNumber(compareValue, condition.getCompareUnit());
                break;
            case "TEXT":
            case "RATIO":
            default:
                compareTarget = compareValue;
                break;
        }

        if (compareTarget == null) {
            return false;
        }

        // Perform comparison
        return compare(fieldValue, operator, compareTarget);
    }

    /**
     * Get field value from data map using field code
     */
    private Object getFieldValue(String fieldCode, Map<String, Object> data) {
        if (fieldCode == null || data == null) {
            return null;
        }

        // Support nested field access using dot notation
        String[] parts = fieldCode.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }

        return current;
    }

    /**
     * Parse number with unit conversion
     */
    private Object parseNumber(String value, String unit) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            BigDecimal number = new BigDecimal(value);

            // Unit conversion (simplified)
            if (unit != null) {
                switch (unit) {
                    case "WAN":
                        number = number.multiply(new BigDecimal("10000"));
                        break;
                    case "ZHAO":
                        number = number.multiply(new BigDecimal("1000000"));
                        break;
                    default:
                        break;
                }
            }

            return number;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Compare two values using the specified operator
     */
    private boolean compare(Object fieldValue, String operator, Object compareTarget) {
        if (fieldValue == null || compareTarget == null) {
            return false;
        }

        try {
            if (fieldValue instanceof Number && compareTarget instanceof Number) {
                BigDecimal fieldNum = new BigDecimal(fieldValue.toString());
                BigDecimal compareNum = new BigDecimal(compareTarget.toString());

                int comparison = fieldNum.compareTo(compareNum);

                switch (operator) {
                    case "GT": return comparison > 0;
                    case "LT": return comparison < 0;
                    case "EQ": return comparison == 0;
                    case "GE": return comparison >= 0;
                    case "LE": return comparison <= 0;
                    case "NE": return comparison != 0;
                    default: return false;
                }
            } else {
                // String comparison
                String fieldStr = fieldValue.toString();
                String compareStr = compareTarget.toString();

                int comparison = fieldStr.compareTo(compareStr);

                switch (operator) {
                    case "GT": return comparison > 0;
                    case "LT": return comparison < 0;
                    case "EQ": return fieldStr.equals(compareStr);
                    case "GE": return comparison >= 0;
                    case "LE": return comparison <= 0;
                    case "NE": return !fieldStr.equals(compareStr);
                    default: return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
}