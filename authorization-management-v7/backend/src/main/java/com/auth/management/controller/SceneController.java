package com.auth.management.controller;

import com.auth.management.dto.request.BatchDeleteRequest;
import com.auth.management.dto.request.CreateSceneRequest;
import com.auth.management.dto.response.*;
import com.auth.management.service.SceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Scene Controller
 */
@RestController
@RequestMapping("/api/v1/auth-letters/{authLetterId}/scenes")
@Api(tags = "Scene Management")
public class SceneController {

    @Autowired
    private SceneService sceneService;

    @GetMapping
    @ApiOperation("Query scene list")
    public ApiResponse<PageResponse<SceneListResponse>> queryList(
            @PathVariable Long authLetterId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResponse<SceneListResponse> result = sceneService.queryList(authLetterId, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get scene detail")
    public ApiResponse<SceneDetailResponse> getDetail(
            @PathVariable Long authLetterId,
            @PathVariable Long id) {
        SceneDetailResponse result = sceneService.getDetail(authLetterId, id);
        return ApiResponse.success(result);
    }

    @PostMapping
    @ApiOperation("Create scene")
    public ApiResponse<CreateResponse> create(
            @PathVariable Long authLetterId,
            @Valid @RequestBody CreateSceneRequest request) {
        Long id = sceneService.create(authLetterId, request);
        return ApiResponse.success(new CreateResponse(id));
    }

    @PutMapping("/{id}")
    @ApiOperation("Update scene")
    public ApiResponse<Void> update(
            @PathVariable Long authLetterId,
            @PathVariable Long id,
            @Valid @RequestBody CreateSceneRequest request) {
        sceneService.update(authLetterId, id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete scene")
    public ApiResponse<Void> delete(
            @PathVariable Long authLetterId,
            @PathVariable Long id) {
        sceneService.delete(authLetterId, id);
        return ApiResponse.success();
    }

    @PostMapping("/batch-delete")
    @ApiOperation("Batch delete scenes")
    public ApiResponse<Void> batchDelete(
            @PathVariable Long authLetterId,
            @Valid @RequestBody BatchDeleteRequest request) {
        sceneService.batchDelete(authLetterId, request.getIds());
        return ApiResponse.success();
    }
}