package com.auth.management.controller;

import com.auth.management.dto.request.SceneMatchRequest;
import com.auth.management.dto.response.ApiResponse;
import com.auth.management.dto.response.SceneMatchResponse;
import com.auth.management.service.SceneMatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Scene Match Controller
 */
@RestController
@RequestMapping("/api/v1")
@Api(tags = "Scene Match")
public class SceneMatchController {

    @Autowired
    private SceneMatchService sceneMatchService;

    @PostMapping("/scene-match")
    @ApiOperation("Match scenes based on input data")
    public ApiResponse<SceneMatchResponse> match(@Valid @RequestBody SceneMatchRequest request) {
        SceneMatchResponse result = sceneMatchService.match(request);
        return ApiResponse.success(result);
    }
}