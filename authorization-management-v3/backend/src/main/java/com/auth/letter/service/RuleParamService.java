package com.auth.letter.service;

import com.auth.letter.dto.PageResult;
import com.auth.letter.dto.RuleParamDTO;
import com.auth.letter.dto.RuleParamQueryDTO;
import com.auth.letter.dto.RuleParamListVO;

import java.util.List;

/**
 * 规则参数服务接口
 */
public interface RuleParamService {

    /**
     * 分页查询规则参数列表
     */
    PageResult<RuleParamListVO> queryList(RuleParamQueryDTO queryDTO);

    /**
     * 获取规则参数详情
     */
    RuleParamListVO getDetail(Long id);

    /**
     * 创建规则参数
     */
    Long create(RuleParamDTO dto);

    /**
     * 更新规则参数
     */
    void update(Long id, RuleParamDTO dto);

    /**
     * 批量激活
     */
    int batchActivate(List<Long> ids);

    /**
     * 批量失效
     */
    int batchDeactivate(List<Long> ids);
}