package com.auth.letter.service;

import com.auth.letter.dto.*;

public interface AttachmentService {
    PageResult<AttachmentVO> queryList(Long authLetterId, int pageNum, int pageSize);
    Long create(Long authLetterId, AttachmentVO dto);
    void delete(Long id);
}