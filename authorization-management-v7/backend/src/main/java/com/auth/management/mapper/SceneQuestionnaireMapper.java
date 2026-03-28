package com.auth.management.mapper;

import com.auth.management.entity.SceneQuestionnaire;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Scene Questionnaire Mapper
 */
@Mapper
public interface SceneQuestionnaireMapper extends BaseMapper<SceneQuestionnaire> {

    /**
     * Select by scene id
     */
    List<SceneQuestionnaire> selectBySceneId(@Param("sceneId") Long sceneId);

    /**
     * Delete by scene id
     */
    Integer deleteBySceneId(@Param("sceneId") Long sceneId);
}