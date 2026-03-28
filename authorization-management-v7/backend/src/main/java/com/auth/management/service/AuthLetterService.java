package com.auth.management.service;

import com.auth.management.dto.request.BatchAuthLetterRequest;
import com.auth.management.dto.request.CreateAuthLetterRequest;
import com.auth.management.dto.response.*;

/**
 * Authorization Letter Service
 */
public interface AuthLetterService {

    /**
     * Query authorization letters with pagination
     */
    PageResponse<AuthLetterListResponse> queryList(String name, String authObjectLevel,
                                                     String applicableRegion, String authPublishLevel,
                                                     String authPublishOrg, Integer publishYear,
                                                     String status, Integer pageNum, Integer pageSize);

    /**
     * Get authorization letter detail
     */
    AuthLetterDetailResponse getDetail(Long id);

    /**
     * Create authorization letter
     */
    Long create(CreateAuthLetterRequest request);

    /**
     * Update authorization letter
     */
    void update(Long id, CreateAuthLetterRequest request);

    /**
     * Publish authorization letter
     */
    void publish(Long id);

    /**
     * Invalidate authorization letter
     */
    void invalidate(Long id);

    /**
     * Delete authorization letter
     */
    void delete(Long id);

    /**
     * Batch operation
     */
    BatchOperationResponse batchOperation(BatchAuthLetterRequest request);

    /**
     * Query operation logs
     */
    PageResponse<OperationLogResponse> queryLogs(Long authLetterId, Integer pageNum, Integer pageSize);
}