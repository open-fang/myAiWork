package com.auth.management.service;

import com.auth.management.dto.request.BatchDeleteRequest;
import com.auth.management.dto.request.CreateSceneRequest;
import com.auth.management.dto.response.*;

import java.util.List;

/**
 * Scene Service
 */
public interface SceneService {

    /**
     * Query scenes with pagination
     */
    PageResponse<SceneListResponse> queryList(Long authLetterId, Integer pageNum, Integer pageSize);

    /**
     * Get scene detail
     */
    SceneDetailResponse getDetail(Long authLetterId, Long id);

    /**
     * Create scene
     */
    Long create(Long authLetterId, CreateSceneRequest request);

    /**
     * Update scene
     */
    void update(Long authLetterId, Long id, CreateSceneRequest request);

    /**
     * Delete scene
     */
    void delete(Long authLetterId, Long id);

    /**
     * Batch delete scenes
     */
    void batchDelete(Long authLetterId, List<Long> ids);

    /**
     * Get scenes by auth letter id for matching
     */
    List<SceneDetailResponse> getScenesByAuthLetterId(Long authLetterId);
}