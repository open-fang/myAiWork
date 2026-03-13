package com.auth.letter.service;

import com.auth.letter.dto.SceneMatchRequestDTO;
import com.auth.letter.dto.SceneMatchResultVO;

import java.util.List;

/**
 * Scene Match Service Interface
 */
public interface SceneMatchService {

    /**
     * Match scenes based on input parameters
     */
    List<SceneMatchResultVO> matchScenes(SceneMatchRequestDTO request);
}