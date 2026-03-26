package com.auth.letter.dao;

import com.auth.letter.entity.RuleCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规则条件DAO接口
 */
public interface RuleConditionDao {

    /**
     * 根据规则ID查询条件列表
     */
    List<RuleCondition> selectByRuleId(@Param("ruleId") Long ruleId);

    /**
     * 根据ID查询条件
     */
    RuleCondition selectById(@Param("id") Long id);

    /**
     * 插入条件
     */
    int insert(RuleCondition condition);

    /**
     * 批量插入条件
     */
    int batchInsert(@Param("list") List<RuleCondition> list);

    /**
     * 更新条件
     */
    int update(RuleCondition condition);

    /**
     * 删除条件
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据规则ID删除条件
     */
    int deleteByRuleId(@Param("ruleId") Long ruleId);
}