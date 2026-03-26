package com.auth.letter.service.impl;

import com.auth.letter.common.PageResult;
import com.auth.letter.dao.RuleConditionDao;
import com.auth.letter.dao.RuleDao;
import com.auth.letter.dao.SceneDao;
import com.auth.letter.dto.request.SceneRequest;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.RuleCondition;
import com.auth.letter.entity.Scene;
import com.auth.letter.service.SceneService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景服务实现类
 */
@Service
public class SceneServiceImpl implements SceneService {

    @Autowired
    private SceneDao sceneDao;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private RuleConditionDao ruleConditionDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResult<Scene> list(Long authLetterId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Scene> list = sceneDao.selectList(authLetterId, offset, pageSize);
        long total = sceneDao.countList(authLetterId);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public Scene getById(Long id) {
        return sceneDao.selectById(id);
    }

    @Override
    @Transactional
    public Long create(Long authLetterId, SceneRequest request, String operator) {
        Scene scene = new Scene();
        scene.setAuthLetterId(authLetterId);
        scene.setSceneName(request.getSceneName());
        scene.setIndustry(listToJson(request.getIndustry()));
        scene.setBusinessScene(request.getBusinessScene());
        scene.setDecisionLevel(request.getDecisionLevel());
        scene.setSpecificRule(request.getSpecificRule());
        scene.setQuestionnaire(listToJson(request.getQuestionnaire()));
        scene.setCreatedBy(operator);

        sceneDao.insert(scene);

        // 保存规则
        if (request.getRules() != null && !request.getRules().isEmpty()) {
            saveRules(scene.getId(), request.getRules(), operator);
        }

        return scene.getId();
    }

    @Override
    @Transactional
    public void update(Long id, SceneRequest request, String operator) {
        Scene scene = sceneDao.selectById(id);
        if (scene == null) {
            throw new RuntimeException("场景不存在");
        }

        scene.setSceneName(request.getSceneName());
        scene.setIndustry(listToJson(request.getIndustry()));
        scene.setBusinessScene(request.getBusinessScene());
        scene.setDecisionLevel(request.getDecisionLevel());
        scene.setSpecificRule(request.getSpecificRule());
        scene.setQuestionnaire(listToJson(request.getQuestionnaire()));
        scene.setUpdatedBy(operator);

        sceneDao.update(scene);

        // 删除旧规则，保存新规则
        deleteRules(id);
        if (request.getRules() != null && !request.getRules().isEmpty()) {
            saveRules(id, request.getRules(), operator);
        }
    }

    @Override
    @Transactional
    public void delete(Long id, String operator) {
        sceneDao.deleteById(id);
        deleteRules(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids, String operator) {
        if (ids != null && !ids.isEmpty()) {
            sceneDao.deleteByIds(ids);
            for (Long id : ids) {
                deleteRules(id);
            }
        }
    }

    @Override
    public List<Scene> getByAuthLetterId(Long authLetterId) {
        return sceneDao.selectByAuthLetterId(authLetterId);
    }

    private String listToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private void saveRules(Long sceneId, List<SceneRequest.RuleDto> rules, String operator) {
        for (SceneRequest.RuleDto ruleDto : rules) {
            Rule rule = new Rule();
            rule.setSceneId(sceneId);
            rule.setLogicType(ruleDto.getLogicType());
            rule.setCreatedBy(operator);
            ruleDao.insert(rule);

            if (ruleDto.getConditions() != null && !ruleDto.getConditions().isEmpty()) {
                List<RuleCondition> conditions = new ArrayList<>();
                for (SceneRequest.ConditionDto conditionDto : ruleDto.getConditions()) {
                    RuleCondition condition = new RuleCondition();
                    condition.setRuleId(rule.getId());
                    condition.setParentConditionId(conditionDto.getParentConditionId());
                    condition.setFieldCode(conditionDto.getFieldCode());
                    condition.setOperator(conditionDto.getOperator());
                    condition.setCompareType(conditionDto.getCompareType());
                    condition.setCompareFieldCode(conditionDto.getCompareFieldCode());
                    condition.setCompareValue(conditionDto.getCompareValue());
                    condition.setCompareUnit(conditionDto.getCompareUnit());
                    condition.setLogicType(conditionDto.getLogicType() != null ? conditionDto.getLogicType() : "AND");
                    condition.setIsGroup(conditionDto.getIsGroup() != null ? conditionDto.getIsGroup() : 0);
                    condition.setGroupLogicType(conditionDto.getGroupLogicType());
                    condition.setSortOrder(conditionDto.getSortOrder() != null ? conditionDto.getSortOrder() : 0);
                    condition.setCreatedBy(operator);
                    conditions.add(condition);
                }
                ruleConditionDao.batchInsert(conditions);
            }
        }
    }

    private void deleteRules(Long sceneId) {
        List<Rule> rules = ruleDao.selectBySceneId(sceneId);
        for (Rule rule : rules) {
            ruleConditionDao.deleteByRuleId(rule.getId());
            ruleDao.deleteById(rule.getId());
        }
    }
}