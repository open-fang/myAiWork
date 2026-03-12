package com.auth.letter.service;

import com.auth.letter.dto.*;
import java.util.List;

public interface AuthLetterService {
    PageResult<AuthLetterListVO> queryList(AuthLetterQueryDTO queryDTO);
    AuthLetterDetailVO getDetail(Long id);
    Long create(AuthLetterDetailDTO dto);
    void update(Long id, AuthLetterDetailDTO dto);
    void delete(Long id);
    void publish(Long id);
    void expire(Long id);
    int batchPublish(List<Long> ids);
    int batchExpire(List<Long> ids);
    int batchDelete(List<Long> ids);
    List<LookupValue> getLookupValues(String code);
    Object getOrgTree();
}