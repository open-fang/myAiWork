package com.auth.management.service.impl;

import com.auth.management.dto.request.CreateAttachmentRequest;
import com.auth.management.dto.response.AttachmentResponse;
import com.auth.management.dto.response.PageResponse;
import com.auth.management.entity.AuthLetterAttachment;
import com.auth.management.exception.BusinessException;
import com.auth.management.exception.ErrorCode;
import com.auth.management.repository.AttachmentRepository;
import com.auth.management.service.AttachmentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Attachment Service Implementation
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public PageResponse<AttachmentResponse> queryList(Long authLetterId, Integer pageNum, Integer pageSize) {
        Page<AuthLetterAttachment> page = new Page<>(pageNum, pageSize);
        Page<AuthLetterAttachment> result = attachmentRepository.findPage(page, authLetterId);

        List<AttachmentResponse> list = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(list, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public Long upload(Long authLetterId, CreateAttachmentRequest request) {
        AuthLetterAttachment attachment = new AuthLetterAttachment();
        attachment.setAuthLetterId(authLetterId);
        attachment.setDocId(request.getDocId());
        attachment.setDocName(request.getDocName());
        attachment.setDocType(request.getDocType());
        attachment.setIsEncrypted(0);

        return attachmentRepository.insert(attachment);
    }

    @Override
    public void download(Long authLetterId, Long id, HttpServletResponse response) {
        AuthLetterAttachment attachment = attachmentRepository.findById(id);
        if (attachment == null || !attachment.getAuthLetterId().equals(authLetterId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Attachment not found");
        }

        // Note: Actual file download logic should be implemented here
        // This is a placeholder that sets response headers
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + attachment.getDocName() + "\"");
            // Write file content to response output stream
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to download attachment");
        }
    }

    @Override
    public void delete(Long authLetterId, Long id) {
        AuthLetterAttachment attachment = attachmentRepository.findById(id);
        if (attachment == null || !attachment.getAuthLetterId().equals(authLetterId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Attachment not found");
        }
        attachmentRepository.delete(id);
    }

    @Override
    public void batchDelete(Long authLetterId, List<Long> ids) {
        for (Long id : ids) {
            delete(authLetterId, id);
        }
    }

    private AttachmentResponse convertToResponse(AuthLetterAttachment entity) {
        AttachmentResponse response = new AttachmentResponse();
        response.setId(entity.getId());
        response.setDocId(entity.getDocId());
        response.setDocName(entity.getDocName());
        response.setDocType(entity.getDocType());
        response.setDocTypeName(entity.getDocType()); // Should lookup from lookup table
        response.setIsEncrypted(entity.getIsEncrypted());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedTime(entity.getCreatedTime());
        response.setUpdatedBy(entity.getUpdatedBy());
        response.setUpdatedTime(entity.getUpdatedTime());
        return response;
    }
}