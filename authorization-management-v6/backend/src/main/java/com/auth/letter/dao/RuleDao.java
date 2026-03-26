package com.auth.letter.dao;

import com.auth.letter.entity.Rule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规则DAO接口
 */
public interface RuleDao {

    /**
     * 根据场景ID查询规则列表
     */
    List<Rule> selectBySceneId(@Param("sceneId") Long sceneId);

    /**
     * 根据ID查询规则
     */
    Rule selectById(@Param("id") Long id);

    /**
     * 插入规则
     */
    int insert(Rule rule);

    /**
     * 更新规则
     */
    int update(Rule rule);

    /**
     * 删除规则
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据场景ID删除规则
     */
    int deleteBySceneId(@Param("sceneId") Long sceneId);
}