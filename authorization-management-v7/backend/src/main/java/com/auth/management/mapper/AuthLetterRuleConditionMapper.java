package com.auth.management.mapper;

import com.auth.management.entity.AuthLetterRuleCondition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Authorization Letter Rule Condition Mapper
 */
@Mapper
public interface AuthLetterRuleConditionMapper extends BaseMapper<AuthLetterRuleCondition> {

    /**
     * Select conditions by rule id with recursive children
     */
    List<AuthLetterRuleCondition> selectByRuleId(@Param("ruleId") Long ruleId);

    /**
     * Select children by parent condition id
     */
    List<AuthLetterRuleCondition> selectChildren(@Param("parentConditionId") Long parentConditionId);

    /**
     * Delete by rule id
     */
    Integer deleteByRuleId(@Param("ruleId") Long ruleId);
}