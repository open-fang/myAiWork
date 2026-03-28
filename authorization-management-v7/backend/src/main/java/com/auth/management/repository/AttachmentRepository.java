package com.auth.management.repository;

import com.auth.management.entity.AuthLetterAttachment;
import com.auth.management.mapper.AuthLetterAttachmentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Attachment Repository
 */
@Repository
public class AttachmentRepository {

    @Autowired
    private AuthLetterAttachmentMapper attachmentMapper;

    public AuthLetterAttachment findById(Long id) {
        return attachmentMapper.selectById(id);
    }

    public Page<AuthLetterAttachment> findPage(Page<AuthLetterAttachment> page, Long authLetterId) {
        LambdaQueryWrapper<AuthLetterAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthLetterAttachment::getAuthLetterId, authLetterId);
        wrapper.orderByDesc(AuthLetterAttachment::getCreatedTime);
        return attachmentMapper.selectPage(page, wrapper);
    }

    public Long insert(AuthLetterAttachment attachment) {
        attachmentMapper.insert(attachment);
        return attachment.getId();
    }

    public void update(AuthLetterAttachment attachment) {
        attachmentMapper.updateById(attachment);
    }

    public void delete(Long id) {
        attachmentMapper.deleteById(id);
    }

    public void deleteByIds(List<Long> ids) {
        attachmentMapper.deleteBatchIds(ids);
    }
}