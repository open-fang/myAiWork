package com.auth.letter.service;

import com.auth.letter.common.PageResult;
import com.auth.letter.dto.request.SceneRequest;
import com.auth.letter.entity.Scene;

import java.util.List;

/**
 * 场景服务接口
 */
public interface SceneService {

    /**
     * 查询场景列表
     */
    PageResult<Scene> list(Long authLetterId, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询场景
     */
    Scene getById(Long id);

    /**
     * 创建场景
     */
    Long create(Long authLetterId, SceneRequest request, String operator);

    /**
     * 更新场景
     */
    void update(Long id, SceneRequest request, String operator);

    /**
     * 删除场景
     */
    void delete(Long id, String operator);

    /**
     * 批量删除场景
     */
    void batchDelete(List<Long> ids, String operator);

    /**
     * 根据授权书ID查询所有场景
     */
    List<Scene> getByAuthLetterId(Long authLetterId);
}