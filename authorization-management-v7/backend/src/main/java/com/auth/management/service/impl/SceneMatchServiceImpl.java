package com.auth.management.service.impl;

import com.auth.management.dto.request.SceneMatchRequest;
import com.auth.management.dto.response.SceneDetailResponse;
import com.auth.management.dto.response.SceneMatchResponse;
import com.auth.management.entity.AuthLetter;
import com.auth.management.enums.AuthLetterStatus;
import com.auth.management.exception.BusinessException;
import com.auth.management.exception.ErrorCode;
import com.auth.management.repository.AuthLetterRepository;
import com.auth.management.service.SceneMatchService;
import com.auth.management.service.SceneService;
import com.auth.management.util.RuleEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scene Match Service Implementation
 */
@Service
public class SceneMatchServiceImpl implements SceneMatchService {

    @Autowired
    private AuthLetterRepository authLetterRepository;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private RuleEvaluator ruleEvaluator;

    @Override
    public SceneMatchResponse match(SceneMatchRequest request) {
        // Get auth letter
        AuthLetter authLetter = authLetterRepository.findById(request.getAuthLetterId());
        if (authLetter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Authorization letter not found");
        }

        // Check if published
        if (!AuthLetterStatus.PUBLISHED.getCode().equals(authLetter.getStatus())) {
            throw new BusinessException(ErrorCode.AUTH_LETTER_STATUS_INVALID,
                    "Authorization letter is not in PUBLISHED status");
        }

        // Get all scenes
        List<SceneDetailResponse> scenes = sceneService.getScenesByAuthLetterId(request.getAuthLetterId());

        // Match each scene
        List<SceneMatchResponse.MatchedScene> matchedScenes = new ArrayList<>();
        Map<String, Object> data = request.getData();

        for (SceneDetailResponse scene : scenes) {
            if (ruleEvaluator.evaluateScene(scene, data)) {
                SceneMatchResponse.MatchedScene matchedScene = new SceneMatchResponse.MatchedScene();
                matchedScene.setSceneId(scene.getId());
                matchedScene.setSceneName(scene.getSceneName());
                matchedScene.setDecisionLevel(scene.getDecisionLevel());
                matchedScene.setDecisionLevelName(scene.getDecisionLevel());
                matchedScenes.add(matchedScene);
            }
        }

        SceneMatchResponse response = new SceneMatchResponse();
        response.setMatchedScenes(matchedScenes);
        return response;
    }
}