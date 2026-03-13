package com.auth.letter.service.impl;

import com.auth.letter.dto.SceneMatchRequestDTO;
import com.auth.letter.dto.SceneMatchResultVO;
import com.auth.letter.entity.AuthScene;
import com.auth.letter.repository.AuthLookupRepository;
import com.auth.letter.repository.AuthSceneRepository;
import com.auth.letter.service.SceneMatchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scene Match Service Implementation
 */
@Service
@RequiredArgsConstructor
public class SceneMatchServiceImpl implements SceneMatchService {

    private static final Logger log = LoggerFactory.getLogger(SceneMatchServiceImpl.class);

    private final AuthSceneRepository sceneRepository;
    private final AuthLookupRepository lookupRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<SceneMatchResultVO> matchScenes(SceneMatchRequestDTO request) {
        List<SceneMatchResultVO> results = new ArrayList<>();

        // Get all scenes for the authorization letter
        List<AuthScene> scenes = sceneRepository.findByAuthLetterId(request.getAuthLetterId());
        if (scenes.isEmpty()) {
            return results;
        }

        Map<String, Object> params = request.getParams();
        if (params == null) {
            params = new java.util.HashMap<>();
        }

        for (AuthScene scene : scenes) {
            if (matchConditions(scene.getConditionGroups(), params)) {
                SceneMatchResultVO result = new SceneMatchResultVO();
                result.setSceneId(scene.getId());
                result.setSceneName(scene.getName());
                result.setDecisionLevel(scene.getDecisionLevel());
                result.setDecisionLevelText(getLookupName(scene.getDecisionLevel(), "DECISION_LEVEL"));
                results.add(result);
            }
        }

        return results;
    }

    private boolean matchConditions(String conditionGroupsJson, Map<String, Object> params) {
        if (conditionGroupsJson == null || conditionGroupsJson.isEmpty()) {
            return true; // No conditions means always match
        }

        try {
            JsonNode conditionGroups = objectMapper.readTree(conditionGroupsJson);

            // Handle case where JSON is stored as string (double-encoded)
            if (conditionGroups.isTextual()) {
                conditionGroups = objectMapper.readTree(conditionGroups.asText());
            }

            return evaluateConditionGroups(conditionGroups, params);
        } catch (Exception e) {
            log.error("Error parsing condition JSON: {}", e.getMessage());
            return false;
        }
    }

    private boolean evaluateConditionGroups(JsonNode conditionGroups, Map<String, Object> params) {
        if (!conditionGroups.has("conditions") || !conditionGroups.get("conditions").isArray()) {
            return true;
        }

        String logicOperator = conditionGroups.has("logicOperator")
                ? conditionGroups.get("logicOperator").asText()
                : "AND";

        JsonNode conditions = conditionGroups.get("conditions");
        List<Boolean> results = new ArrayList<>();

        for (JsonNode condition : conditions) {
            if (condition.has("conditions")) {
                // Nested condition group
                results.add(evaluateConditionGroups(condition, params));
            } else {
                // Single condition
                results.add(evaluateCondition(condition, params));
            }
        }

        if (results.isEmpty()) {
            return true;
        }

        if ("OR".equals(logicOperator)) {
            return results.stream().anyMatch(r -> r);
        } else {
            return results.stream().allMatch(r -> r);
        }
    }

    private boolean evaluateCondition(JsonNode condition, Map<String, Object> params) {
        String fieldName = condition.has("fieldName") ? condition.get("fieldName").asText() : null;
        String operator = condition.has("operator") ? condition.get("operator").asText() : null;

        if (fieldName == null || operator == null) {
            return true;
        }

        Object paramValue = params.get(fieldName);
        if (paramValue == null) {
            return false;
        }

        JsonNode compareConfig = condition.get("compareConfig");
        if (compareConfig == null) {
            return true;
        }

        String compareType = compareConfig.has("type") ? compareConfig.get("type").asText() : "TEXT";
        JsonNode valueNode = compareConfig.get("value");

        try {
            if ("COMPARE_FIELD".equals(compareType)) {
                String targetField = valueNode.asText();
                Object targetValue = params.get(targetField);
                return compareValues(paramValue, targetValue, operator);
            } else if ("NUMBER".equals(compareType)) {
                double paramNum = toNumber(paramValue);
                double targetNum = valueNode.asDouble();
                return compareNumbers(paramNum, targetNum, operator);
            } else {
                String targetText = valueNode.asText();
                return compareTexts(String.valueOf(paramValue), targetText, operator);
            }
        } catch (Exception e) {
            log.error("Error evaluating condition: {}", e.getMessage());
            return false;
        }
    }

    private boolean compareValues(Object value1, Object value2, String operator) {
        if (value1 == null || value2 == null) {
            return "OP_NEQ".equals(operator);
        }

        String str1 = String.valueOf(value1);
        String str2 = String.valueOf(value2);

        try {
            double num1 = Double.parseDouble(str1);
            double num2 = Double.parseDouble(str2);
            return compareNumbers(num1, num2, operator);
        } catch (NumberFormatException e) {
            return compareTexts(str1, str2, operator);
        }
    }

    private boolean compareNumbers(double num1, double num2, String operator) {
        switch (operator) {
            case "OP_GT": return num1 > num2;
            case "OP_LT": return num1 < num2;
            case "OP_EQ": return num1 == num2;
            case "OP_GTE": return num1 >= num2;
            case "OP_LTE": return num1 <= num2;
            case "OP_NEQ": return num1 != num2;
            default: return false;
        }
    }

    private boolean compareTexts(String text1, String text2, String operator) {
        int cmp = text1.compareTo(text2);
        switch (operator) {
            case "OP_GT": return cmp > 0;
            case "OP_LT": return cmp < 0;
            case "OP_EQ": return cmp == 0;
            case "OP_GTE": return cmp >= 0;
            case "OP_LTE": return cmp <= 0;
            case "OP_NEQ": return cmp != 0;
            default: return false;
        }
    }

    private double toNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private String getLookupName(String code, String lookupType) {
        if (code == null) return "";
        return lookupRepository.findByLookupTypeOrderBySortOrderAsc(lookupType).stream()
                .filter(l -> code.equals(l.getLookupCode()))
                .findFirst()
                .map(l -> l.getLookupName())
                .orElse(code);
    }
}