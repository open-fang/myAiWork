package com.auth.management.repository;

import com.auth.management.entity.SceneQuestionnaire;
import com.auth.management.mapper.SceneQuestionnaireMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Scene Questionnaire Repository
 */
@Repository
public class SceneQuestionnaireRepository {

    @Autowired
    private SceneQuestionnaireMapper sceneQuestionnaireMapper;

    public List<SceneQuestionnaire> findBySceneId(Long sceneId) {
        return sceneQuestionnaireMapper.selectBySceneId(sceneId);
    }

    public Long insert(SceneQuestionnaire sq) {
        sceneQuestionnaireMapper.insert(sq);
        return sq.getId();
    }

    public void deleteBySceneId(Long sceneId) {
        sceneQuestionnaireMapper.deleteBySceneId(sceneId);
    }
}