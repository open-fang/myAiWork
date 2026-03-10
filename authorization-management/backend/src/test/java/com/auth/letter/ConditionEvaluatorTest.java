package com.auth.letter;

import com.auth.letter.entity.Condition;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.LogicOperator;
import com.auth.letter.service.ConditionEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest {

    private ConditionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ConditionEvaluator();
    }

    @Test
    @DisplayName("简单条件 - 等于")
    void testSimpleConditionEquals() {
        List<Condition> conditions = new ArrayList<>();
        Condition condition = new Condition();
        condition.setFieldName("status");
        condition.setOperator("EQ");
        condition.setValue("approved");
        conditions.add(condition);

        Map<String, Object> fields = new HashMap<>();
        fields.put("status", "approved");

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    @Test
    @DisplayName("简单条件 - 不等于")
    void testSimpleConditionNotEquals() {
        List<Condition> conditions = new ArrayList<>();
        Condition condition = new Condition();
        condition.setFieldName("status");
        condition.setOperator("NE");
        condition.setValue("rejected");
        conditions.add(condition);

        Map<String, Object> fields = new HashMap<>();
        fields.put("status", "approved");

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    @Test
    @DisplayName("简单条件 - 大于")
    void testSimpleConditionGreaterThan() {
        List<Condition> conditions = new ArrayList<>();
        Condition condition = new Condition();
        condition.setFieldName("amount");
        condition.setOperator("GT");
        condition.setValue("1000");
        conditions.add(condition);

        Map<String, Object> fields = new HashMap<>();
        fields.put("amount", 1500);

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    @Test
    @DisplayName("简单条件 - 小于等于")
    void testSimpleConditionLessThanOrEqual() {
        List<Condition> conditions = new ArrayList<>();
        Condition condition = new Condition();
        condition.setFieldName("amount");
        condition.setOperator("LE");
        condition.setValue("1000");
        conditions.add(condition);

        Map<String, Object> fields = new HashMap<>();
        fields.put("amount", 1000);

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    @Test
    @DisplayName("简单条件 - IN操作符")
    void testSimpleConditionIn() {
        List<Condition> conditions = new ArrayList<>();
        Condition condition = new Condition();
        condition.setFieldName("level");
        condition.setOperator("IN");
        condition.setValue("A,B,C");
        conditions.add(condition);

        Map<String, Object> fields = new HashMap<>();
        fields.put("level", "B");

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    @Test
    @DisplayName("简单条件 - LIKE操作符")
    void testSimpleConditionLike() {
        List<Condition> conditions = new ArrayList<>();
        Condition condition = new Condition();
        condition.setFieldName("name");
        condition.setOperator("LIKE");
        condition.setValue("张");
        conditions.add(condition);

        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "张三");

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    @Test
    @DisplayName("嵌套条件 - AND逻辑")
    void testNestedConditionAnd() {
        List<Condition> conditions = createNestedConditions(LogicOperator.AND);

        Map<String, Object> fields = new HashMap<>();
        fields.put("status", "approved");
        fields.put("amount", 5000);

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    @Test
    @DisplayName("嵌套条件 - OR逻辑")
    void testNestedConditionOr() {
        List<Condition> conditions = createNestedConditions(LogicOperator.OR);

        // 只满足一个条件
        Map<String, Object> fields = new HashMap<>();
        fields.put("status", "approved");
        fields.put("amount", 20000); // amount > 10000 为false

        assertTrue(evaluator.evaluate(conditions, fields));
    }

    private List<Condition> createNestedConditions(LogicOperator operator) {
        List<Condition> conditions = new ArrayList<>();

        Condition parentCondition = new Condition();
        parentCondition.setLogicOperator(operator);

        Condition child1 = new Condition();
        child1.setFieldName("status");
        child1.setOperator("EQ");
        child1.setValue("approved");
        child1.setParent(parentCondition);

        Condition child2 = new Condition();
        child2.setFieldName("amount");
        child2.setOperator("LT");
        child2.setValue("10000");
        child2.setParent(parentCondition);

        parentCondition.setChildren(Arrays.asList(child1, child2));
        conditions.add(parentCondition);

        return conditions;
    }
}