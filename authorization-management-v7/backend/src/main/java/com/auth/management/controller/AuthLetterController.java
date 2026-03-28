package com.auth.management.controller;

import com.auth.management.dto.request.BatchAuthLetterRequest;
import com.auth.management.dto.request.CreateAuthLetterRequest;
import com.auth.management.dto.response.*;
import com.auth.management.service.AuthLetterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Authorization Letter Controller
 */
@RestController
@RequestMapping("/api/v1/auth-letters")
@Api(tags = "Authorization Letter Management")
public class AuthLetterController {

    @Autowired
    private AuthLetterService authLetterService;

    @GetMapping
    @ApiOperation("Query authorization letter list")
    public ApiResponse<PageResponse<AuthLetterListResponse>> queryList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String authObjectLevel,
            @RequestParam(required = false) String applicableRegion,
            @RequestParam(required = false) String authPublishLevel,
            @RequestParam(required = false) String authPublishOrg,
            @RequestParam(required = false) Integer publishYear,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResponse<AuthLetterListResponse> result = authLetterService.queryList(
                name, authObjectLevel, applicableRegion, authPublishLevel,
                authPublishOrg, publishYear, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get authorization letter detail")
    public ApiResponse<AuthLetterDetailResponse> getDetail(@PathVariable Long id) {
        AuthLetterDetailResponse result = authLetterService.getDetail(id);
        return ApiResponse.success(result);
    }

    @PostMapping
    @ApiOperation("Create authorization letter")
    public ApiResponse<CreateResponse> create(@Valid @RequestBody CreateAuthLetterRequest request) {
        Long id = authLetterService.create(request);
        return ApiResponse.success(new CreateResponse(id));
    }

    @PutMapping("/{id}")
    @ApiOperation("Update authorization letter")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CreateAuthLetterRequest request) {
        authLetterService.update(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/publish")
    @ApiOperation("Publish authorization letter")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        authLetterService.publish(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/invalidate")
    @ApiOperation("Invalidate authorization letter")
    public ApiResponse<Void> invalidate(@PathVariable Long id) {
        authLetterService.invalidate(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete authorization letter")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authLetterService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch")
    @ApiOperation("Batch operation for authorization letters")
    public ApiResponse<BatchOperationResponse> batchOperation(@Valid @RequestBody BatchAuthLetterRequest request) {
        BatchOperationResponse result = authLetterService.batchOperation(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}/logs")
    @ApiOperation("Query operation logs for authorization letter")
    public ApiResponse<PageResponse<OperationLogResponse>> queryLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResponse<OperationLogResponse> result = authLetterService.queryLogs(id, pageNum, pageSize);
        return ApiResponse.success(result);
    }
}