package com.auth.letter.service;

import com.auth.letter.dto.*;
import java.util.List;

/**
 * Rule Parameter Service Interface
 */
public interface RuleParamService {

    /**
     * Query rule parameter list with pagination
     */
    PageResult<RuleParamListVO> queryList(RuleParamQueryDTO queryDTO);

    /**
     * Get rule parameter by id
     */
    RuleParamDTO getById(Long id);

    /**
     * Create rule parameter
     */
    Long create(RuleParamDTO dto);

    /**
     * Update rule parameter
     */
    void update(Long id, RuleParamDTO dto);

    /**
     * Delete rule parameter
     */
    void delete(Long id);

    /**
     * Batch activate rule parameters
     */
    int batchActivate(List<Long> ids);

    /**
     * Batch deactivate rule parameters
     */
    int batchDeactivate(List<Long> ids);

    /**
     * Get all active rule parameters for dropdown
     */
    List<RuleParamListVO> getAllActive();
}