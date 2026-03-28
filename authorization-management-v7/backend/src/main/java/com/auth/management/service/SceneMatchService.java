package com.auth.management.service;

import com.auth.management.dto.request.SceneMatchRequest;
import com.auth.management.dto.response.SceneMatchResponse;

/**
 * Scene Match Service
 */
public interface SceneMatchService {

    /**
     * Match scenes based on input data
     * @param request the match request containing auth letter id and data
     * @return the matched scenes
     */
    SceneMatchResponse match(SceneMatchRequest request);
}