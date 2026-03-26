package com.auth.letter.service;

import com.auth.letter.common.PageResult;
import com.auth.letter.dto.request.RuleParamRequest;
import com.auth.letter.entity.RuleParam;

import java.util.List;

/**
 * 规则参数服务接口
 */
public interface RuleParamService {

    /**
     * 查询规则参数列表
     */
    PageResult<RuleParam> list(String name, String nameEn, String status, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询规则参数
     */
    RuleParam getById(Long id);

    /**
     * 查询所有生效的规则参数
     */
    List<RuleParam> getAllActive();

    /**
     * 创建规则参数
     */
    Long create(RuleParamRequest request, String operator);

    /**
     * 更新规则参数
     */
    void update(Long id, RuleParamRequest request, String operator);

    /**
     * 删除规则参数
     */
    void delete(Long id, String operator);

    /**
     * 批量更新状态
     */
    void batchUpdateStatus(List<Long> ids, String status, String operator);
}