package com.auth.letter.controller;

import com.auth.letter.common.PageResult;
import com.auth.letter.common.Result;
import com.auth.letter.dto.request.SceneMatchRequest;
import com.auth.letter.dto.response.MatchedSceneResponse;
import com.auth.letter.entity.AuthLetterLog;
import com.auth.letter.dao.AuthLetterLogDao;
import com.auth.letter.service.SceneMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 场景匹配控制器
 */
@RestController
@RequestMapping
public class SceneMatchController {

    @Autowired
    private SceneMatchService sceneMatchService;

    @Autowired
    private AuthLetterLogDao authLetterLogDao;

    /**
     * 场景匹配
     */
    @PostMapping("/scene-match")
    public Result<MatchResult> match(@RequestBody SceneMatchRequest request) {
        List<MatchedSceneResponse> matchedScenes = sceneMatchService.match(request);
        MatchResult result = new MatchResult();
        result.setMatchedScenes(matchedScenes);
        return Result.success(result);
    }

    /**
     * 查询操作日志
     */
    @GetMapping("/auth-letters/{authLetterId}/logs")
    public Result<PageResult<AuthLetterLog>> getLogs(
            @PathVariable Long authLetterId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<AuthLetterLog> list = authLetterLogDao.selectList(authLetterId, offset, pageSize);
        long total = authLetterLogDao.countList(authLetterId);
        return Result.success(new PageResult<>(list, total, pageNum, pageSize));
    }

    /**
     * 匹配结果
     */
    public static class MatchResult {
        private List<MatchedSceneResponse> matchedScenes;

        public List<MatchedSceneResponse> getMatchedScenes() {
            return matchedScenes;
        }

        public void setMatchedScenes(List<MatchedSceneResponse> matchedScenes) {
            this.matchedScenes = matchedScenes;
        }
    }
}