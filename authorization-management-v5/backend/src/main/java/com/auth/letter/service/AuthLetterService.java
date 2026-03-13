package com.auth.letter.service;

import com.auth.letter.dto.*;

import java.util.List;

/**
 * Authorization Letter Service Interface
 */
public interface AuthLetterService {

    /**
     * Query authorization letter list with pagination
     */
    PageResult<AuthLetterListVO> queryList(AuthLetterQueryDTO queryDTO);

    /**
     * Get authorization letter detail by id
     */
    AuthLetterDetailVO getDetail(Long id);

    /**
     * Create authorization letter
     */
    Long create(AuthLetterDetailDTO dto);

    /**
     * Update authorization letter
     */
    void update(Long id, AuthLetterDetailDTO dto);

    /**
     * Delete authorization letter by id
     */
    void delete(Long id);

    /**
     * Publish authorization letter
     */
    void publish(Long id);

    /**
     * Expire authorization letter
     */
    void expire(Long id);

    /**
     * Batch publish authorization letters
     */
    int batchPublish(List<Long> ids);

    /**
     * Batch expire authorization letters
     */
    int batchExpire(List<Long> ids);

    /**
     * Batch delete authorization letters
     */
    int batchDelete(List<Long> ids);

    /**
     * Get operation logs
     */
    PageResult<OperationLogVO> getOperationLogs(Long authLetterId, Integer pageNum, Integer pageSize);
}