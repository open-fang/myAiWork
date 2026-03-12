package com.auth.letter.service;

import com.auth.letter.dto.*;
import java.util.List;

public interface SceneService {
    PageResult<SceneVO> queryList(Long authLetterId, int pageNum, int pageSize);
    Long create(Long authLetterId, SceneDTO dto);
    void update(Long id, SceneDTO dto);
    void delete(Long id);
    int batchDelete(List<Long> ids);
}