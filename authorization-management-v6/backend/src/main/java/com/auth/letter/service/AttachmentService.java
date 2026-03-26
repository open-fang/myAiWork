package com.auth.letter.service;

import com.auth.letter.common.PageResult;
import com.auth.letter.entity.Attachment;

import java.util.List;

/**
 * 附件服务接口
 */
public interface AttachmentService {

    /**
     * 查询附件列表
     */
    PageResult<Attachment> list(Long authLetterId, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询附件
     */
    Attachment getById(Long id);

    /**
     * 创建附件
     */
    Long create(Long authLetterId, String docId, String docName, String docType, String operator);

    /**
     * 删除附件
     */
    void delete(Long id, String operator);

    /**
     * 批量删除附件
     */
    void batchDelete(List<Long> ids, String operator);
}