package com.auth.management.service;

import com.auth.management.dto.request.CreateRuleParamRequest;
import com.auth.management.dto.request.BatchRuleParamStatusRequest;
import com.auth.management.dto.response.PageResponse;
import com.auth.management.dto.response.RuleParamDetailResponse;
import com.auth.management.dto.response.RuleParamListResponse;
import com.auth.management.dto.response.RuleParamOptionResponse;

import java.util.List;

/**
 * Rule Parameter Service
 */
public interface RuleParamService {

    /**
     * Query rule parameters with pagination
     */
    PageResponse<RuleParamListResponse> queryList(String name, String nameEn, String status,
                                                    Integer pageNum, Integer pageSize);

    /**
     * Get rule parameter detail
     */
    RuleParamDetailResponse getDetail(Long id);

    /**
     * Create rule parameter
     */
    Long create(CreateRuleParamRequest request);

    /**
     * Update rule parameter
     */
    void update(Long id, CreateRuleParamRequest request);

    /**
     * Batch update status
     */
    void batchUpdateStatus(BatchRuleParamStatusRequest request);

    /**
     * Delete rule parameter
     */
    void delete(Long id);

    /**
     * Get rule parameter options for dropdown
     */
    List<RuleParamOptionResponse> getOptions(String status);
}