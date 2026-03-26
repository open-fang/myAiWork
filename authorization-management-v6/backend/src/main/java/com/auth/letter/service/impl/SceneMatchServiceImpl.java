package com.auth.letter.service.impl;

import com.auth.letter.dto.request.SceneMatchRequest;
import com.auth.letter.dto.response.MatchedSceneResponse;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.RuleCondition;
import com.auth.letter.entity.Scene;
import com.auth.letter.service.SceneMatchService;
import com.auth.letter.service.SceneService;
import com.auth.letter.dao.RuleDao;
import com.auth.letter.dao.RuleConditionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 场景匹配服务实现类
 */
@Service
public class SceneMatchServiceImpl implements SceneMatchService {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private RuleConditionDao ruleConditionDao;

    @Override
    public List<MatchedSceneResponse> match(SceneMatchRequest request) {
        List<MatchedSceneResponse> result = new ArrayList<>();

        // 获取授权书下所有场景
        List<Scene> scenes = sceneService.getByAuthLetterId(request.getAuthLetterId());
        Map<String, Object> data = request.getData();

        for (Scene scene : scenes) {
            if (matchScene(scene, data)) {
                result.add(new MatchedSceneResponse(scene.getId(), scene.getSceneName(), scene.getDecisionLevel()));
            }
        }

        return result;
    }

    private boolean matchScene(Scene scene, Map<String, Object> data) {
        // 获取场景的规则
        List<Rule> rules = ruleDao.selectBySceneId(scene.getId());

        if (rules.isEmpty()) {
            // 如果没有规则，默认匹配
            return true;
        }

        // 规则之间是AND关系
        for (Rule rule : rules) {
            if (!matchRule(rule, data)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchRule(Rule rule, Map<String, Object> data) {
        List<RuleCondition> conditions = ruleConditionDao.selectByRuleId(rule.getId());

        if (conditions.isEmpty()) {
            return true;
        }

        String logicType = rule.getLogicType();
        boolean result = true;

        for (int i = 0; i < conditions.size(); i++) {
            RuleCondition condition = conditions.get(i);
            boolean conditionResult = matchCondition(condition, data);

            if (i == 0) {
                result = conditionResult;
            } else {
                if ("AND".equals(logicType)) {
                    result = result && conditionResult;
                } else {
                    result = result || conditionResult;
                }
            }
        }

        return result;
    }

    private boolean matchCondition(RuleCondition condition, Map<String, Object> data) {
        String fieldCode = condition.getFieldCode();
        Object fieldValue = data.get(fieldCode);

        if (fieldValue == null) {
            return false;
        }

        String operator = condition.getOperator();
        String compareValue = condition.getCompareValue();

        try {
            double fieldNum = convertToNumber(fieldValue);
            double compareNum = convertToNumber(compareValue);

            switch (operator) {
                case ">":
                    return fieldNum > compareNum;
                case "<":
                    return fieldNum < compareNum;
                case "=":
                    return fieldNum == compareNum;
                case ">=":
                    return fieldNum >= compareNum;
                case "<=":
                    return fieldNum <= compareNum;
                case "!=":
                    return fieldNum != compareNum;
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            // 文本比较
            String fieldStr = String.valueOf(fieldValue);
            switch (operator) {
                case "=":
                    return fieldStr.equals(compareValue);
                case "!=":
                    return !fieldStr.equals(compareValue);
                default:
                    return false;
            }
        }
    }

    private double convertToNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }
}