package com.auth.management.repository;

import com.auth.management.entity.AuthLetterScene;
import com.auth.management.mapper.AuthLetterSceneMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Authorization Letter Scene Repository
 */
@Repository
public class AuthLetterSceneRepository {

    @Autowired
    private AuthLetterSceneMapper authLetterSceneMapper;

    public AuthLetterScene findById(Long id) {
        return authLetterSceneMapper.selectById(id);
    }

    public List<AuthLetterScene> findByAuthLetterId(Long authLetterId) {
        LambdaQueryWrapper<AuthLetterScene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthLetterScene::getAuthLetterId, authLetterId);
        wrapper.orderByAsc(AuthLetterScene::getId);
        return authLetterSceneMapper.selectList(wrapper);
    }

    public Page<AuthLetterScene> findPage(Page<AuthLetterScene> page, Long authLetterId) {
        LambdaQueryWrapper<AuthLetterScene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthLetterScene::getAuthLetterId, authLetterId);
        wrapper.orderByDesc(AuthLetterScene::getCreatedTime);
        return authLetterSceneMapper.selectPage(page, wrapper);
    }

    public Long insert(AuthLetterScene scene) {
        authLetterSceneMapper.insert(scene);
        return scene.getId();
    }

    public void update(AuthLetterScene scene) {
        authLetterSceneMapper.updateById(scene);
    }

    public void delete(Long id) {
        authLetterSceneMapper.deleteById(id);
    }

    public boolean checkSceneNameExists(Long authLetterId, String sceneName, Long excludeId) {
        return authLetterSceneMapper.checkSceneNameExists(authLetterId, sceneName, excludeId) > 0;
    }
}