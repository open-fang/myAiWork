package com.auth.management.controller;

import com.auth.management.dto.request.BatchDeleteRequest;
import com.auth.management.dto.request.BatchRuleParamStatusRequest;
import com.auth.management.dto.request.CreateRuleParamRequest;
import com.auth.management.dto.response.*;
import com.auth.management.service.RuleParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Rule Parameter Controller
 */
@RestController
@RequestMapping("/api/v1/rule-params")
@Api(tags = "Rule Parameter Management")
public class RuleParamController {

    @Autowired
    private RuleParamService ruleParamService;

    @GetMapping
    @ApiOperation("Query rule parameter list")
    public ApiResponse<PageResponse<RuleParamListResponse>> queryList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String nameEn,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResponse<RuleParamListResponse> result = ruleParamService.queryList(name, nameEn, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get rule parameter detail")
    public ApiResponse<RuleParamDetailResponse> getDetail(@PathVariable Long id) {
        RuleParamDetailResponse result = ruleParamService.getDetail(id);
        return ApiResponse.success(result);
    }

    @PostMapping
    @ApiOperation("Create rule parameter")
    public ApiResponse<CreateResponse> create(@Valid @RequestBody CreateRuleParamRequest request) {
        Long id = ruleParamService.create(request);
        return ApiResponse.success(new CreateResponse(id));
    }

    @PutMapping("/{id}")
    @ApiOperation("Update rule parameter")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CreateRuleParamRequest request) {
        ruleParamService.update(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete rule parameter")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        ruleParamService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch-status")
    @ApiOperation("Batch update rule parameter status")
    public ApiResponse<Void> batchUpdateStatus(@Valid @RequestBody BatchRuleParamStatusRequest request) {
        ruleParamService.batchUpdateStatus(request);
        return ApiResponse.success();
    }

    @GetMapping("/options")
    @ApiOperation("Get rule parameter options for dropdown")
    public ApiResponse<List<RuleParamOptionResponse>> getOptions(
            @RequestParam(required = false) String status) {
        List<RuleParamOptionResponse> result = ruleParamService.getOptions(status);
        return ApiResponse.success(result);
    }

    @GetMapping("/active")
    @ApiOperation("Get active rule parameters")
    public ApiResponse<List<RuleParamOptionResponse>> getActiveParams() {
        List<RuleParamOptionResponse> result = ruleParamService.getOptions("ACTIVE");
        return ApiResponse.success(result);
    }
}