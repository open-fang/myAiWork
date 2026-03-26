package com.auth.letter.service;

import com.auth.letter.dto.request.SceneMatchRequest;
import com.auth.letter.dto.response.MatchedSceneResponse;

import java.util.List;

/**
 * 场景匹配服务接口
 */
public interface SceneMatchService {

    /**
     * 场景匹配
     */
    List<MatchedSceneResponse> match(SceneMatchRequest request);
}