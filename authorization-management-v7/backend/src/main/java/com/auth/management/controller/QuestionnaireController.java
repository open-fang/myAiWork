package com.auth.management.controller;

import com.auth.management.dto.request.BatchDeleteRequest;
import com.auth.management.dto.request.CreateQuestionRequest;
import com.auth.management.dto.response.*;
import com.auth.management.service.QuestionnaireService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Questionnaire Controller
 */
@RestController
@RequestMapping("/api/v1/questionnaire-questions")
@Api(tags = "Questionnaire Management")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;

    @GetMapping
    @ApiOperation("Query questionnaire question list")
    public ApiResponse<PageResponse<QuestionListResponse>> queryList(
            @RequestParam(required = false) String questionCode,
            @RequestParam(required = false) String questionText,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String createdTimeFrom,
            @RequestParam(required = false) String createdTimeTo,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) String updatedTimeFrom,
            @RequestParam(required = false) String updatedTimeTo,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResponse<QuestionListResponse> result = questionnaireService.queryList(
                questionCode, questionText, language, createdBy,
                createdTimeFrom, createdTimeTo, updatedBy,
                updatedTimeFrom, updatedTimeTo, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get questionnaire question detail")
    public ApiResponse<QuestionDetailResponse> getDetail(@PathVariable Long id) {
        QuestionDetailResponse result = questionnaireService.getDetail(id);
        return ApiResponse.success(result);
    }

    @PostMapping
    @ApiOperation("Create questionnaire question")
    public ApiResponse<CreateQuestionResponse> create(@Valid @RequestBody CreateQuestionRequest request) {
        CreateQuestionResponse result = questionnaireService.create(request);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update questionnaire question")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CreateQuestionRequest request) {
        questionnaireService.update(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete questionnaire question")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        questionnaireService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch-delete")
    @ApiOperation("Batch delete questionnaire questions")
    public ApiResponse<Void> batchDelete(@Valid @RequestBody BatchDeleteRequest request) {
        questionnaireService.batchDelete(request.getIds());
        return ApiResponse.success();
    }

    @GetMapping("/options")
    @ApiOperation("Get questionnaire question options for dropdown")
    public ApiResponse<List<QuestionOptionResponse>> getOptions(
            @RequestParam(required = false) String language) {
        List<QuestionOptionResponse> result = questionnaireService.getOptions(language);
        return ApiResponse.success(result);
    }
}