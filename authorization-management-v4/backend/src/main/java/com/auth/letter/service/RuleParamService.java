package com.auth.letter.service;

import com.auth.letter.dto.*;
import java.util.List;

public interface RuleParamService {
    PageResult<RuleParamListVO> queryList(RuleParamQueryDTO queryDTO);
    RuleParamDTO getDetail(Long id);
    Long create(RuleParamDTO dto);
    void update(Long id, RuleParamDTO dto);
    int batchActivate(List<Long> ids);
    int batchDeactivate(List<Long> ids);
}