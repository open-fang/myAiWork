package com.auth.letter.service.impl;

import com.auth.letter.common.PageResult;
import com.auth.letter.dao.AttachmentDao;
import com.auth.letter.entity.Attachment;
import com.auth.letter.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 附件服务实现类
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private AttachmentDao attachmentDao;

    @Override
    public PageResult<Attachment> list(Long authLetterId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Attachment> list = attachmentDao.selectList(authLetterId, offset, pageSize);
        long total = attachmentDao.countList(authLetterId);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public Attachment getById(Long id) {
        return attachmentDao.selectById(id);
    }

    @Override
    @Transactional
    public Long create(Long authLetterId, String docId, String docName, String docType, String operator) {
        Attachment attachment = new Attachment();
        attachment.setAuthLetterId(authLetterId);
        attachment.setDocId(docId);
        attachment.setDocName(docName);
        attachment.setDocType(docType);
        attachment.setIsEncrypted(0);
        attachment.setCreatedBy(operator);

        attachmentDao.insert(attachment);
        return attachment.getId();
    }

    @Override
    @Transactional
    public void delete(Long id, String operator) {
        attachmentDao.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids, String operator) {
        if (ids != null && !ids.isEmpty()) {
            attachmentDao.deleteByIds(ids);
        }
    }
}