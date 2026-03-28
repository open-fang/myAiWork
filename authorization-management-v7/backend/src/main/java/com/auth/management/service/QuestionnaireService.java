package com.auth.management.service;

import com.auth.management.dto.request.BatchDeleteRequest;
import com.auth.management.dto.request.CreateQuestionRequest;
import com.auth.management.dto.response.*;

import java.util.List;

/**
 * Questionnaire Service
 */
public interface QuestionnaireService {

    /**
     * Query questionnaire questions with pagination
     */
    PageResponse<QuestionListResponse> queryList(String questionCode, String questionText,
                                                   String language, String createdBy,
                                                   String createdTimeFrom, String createdTimeTo,
                                                   String updatedBy, String updatedTimeFrom,
                                                   String updatedTimeTo,
                                                   Integer pageNum, Integer pageSize);

    /**
     * Get questionnaire question detail
     */
    QuestionDetailResponse getDetail(Long id);

    /**
     * Create questionnaire question
     */
    CreateQuestionResponse create(CreateQuestionRequest request);

    /**
     * Update questionnaire question
     */
    void update(Long id, CreateQuestionRequest request);

    /**
     * Delete questionnaire question
     */
    void delete(Long id);

    /**
     * Batch delete questionnaire questions
     */
    void batchDelete(List<Long> ids);

    /**
     * Get questionnaire question options for dropdown
     */
    List<QuestionOptionResponse> getOptions(String language);
}