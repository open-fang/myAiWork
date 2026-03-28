package com.auth.management.repository;

import com.auth.management.entity.AuthLetterRule;
import com.auth.management.entity.AuthLetterRuleCondition;
import com.auth.management.mapper.AuthLetterRuleConditionMapper;
import com.auth.management.mapper.AuthLetterRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Rule Repository
 */
@Repository
public class RuleRepository {

    @Autowired
    private AuthLetterRuleMapper ruleMapper;

    @Autowired
    private AuthLetterRuleConditionMapper conditionMapper;

    // Rule methods
    public AuthLetterRule findRuleById(Long id) {
        return ruleMapper.selectById(id);
    }

    public List<AuthLetterRule> findRulesBySceneId(Long sceneId) {
        return ruleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AuthLetterRule>()
                        .eq(AuthLetterRule::getSceneId, sceneId)
                        .orderByAsc(AuthLetterRule::getId));
    }

    public Long insertRule(AuthLetterRule rule) {
        ruleMapper.insert(rule);
        return rule.getId();
    }

    public void deleteRule(Long id) {
        ruleMapper.deleteById(id);
    }

    public void deleteRulesBySceneId(Long sceneId) {
        List<AuthLetterRule> rules = findRulesBySceneId(sceneId);
        for (AuthLetterRule rule : rules) {
            deleteConditionsByRuleId(rule.getId());
            ruleMapper.deleteById(rule.getId());
        }
    }

    // Condition methods
    public List<AuthLetterRuleCondition> findConditionsByRuleId(Long ruleId) {
        return conditionMapper.selectByRuleId(ruleId);
    }

    public List<AuthLetterRuleCondition> findChildrenByParentId(Long parentConditionId) {
        return conditionMapper.selectChildren(parentConditionId);
    }

    public Long insertCondition(AuthLetterRuleCondition condition) {
        conditionMapper.insert(condition);
        return condition.getId();
    }

    public void deleteConditionsByRuleId(Long ruleId) {
        conditionMapper.deleteByRuleId(ruleId);
    }
}