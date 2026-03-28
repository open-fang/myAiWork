package com.auth.management.service;

import com.auth.management.dto.request.BatchDeleteRequest;
import com.auth.management.dto.request.CreateAttachmentRequest;
import com.auth.management.dto.response.AttachmentResponse;
import com.auth.management.dto.response.PageResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Attachment Service
 */
public interface AttachmentService {

    /**
     * Query attachments with pagination
     */
    PageResponse<AttachmentResponse> queryList(Long authLetterId, Integer pageNum, Integer pageSize);

    /**
     * Upload attachment
     */
    Long upload(Long authLetterId, CreateAttachmentRequest request);

    /**
     * Download attachment
     */
    void download(Long authLetterId, Long id, HttpServletResponse response);

    /**
     * Delete attachment
     */
    void delete(Long authLetterId, Long id);

    /**
     * Batch delete attachments
     */
    void batchDelete(Long authLetterId, List<Long> ids);
}