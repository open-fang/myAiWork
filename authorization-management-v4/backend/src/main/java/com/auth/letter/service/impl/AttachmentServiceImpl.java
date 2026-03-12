package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthAttachment;
import com.auth.letter.mapper.AuthAttachmentMapper;
import com.auth.letter.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AuthAttachmentMapper authAttachmentMapper;

    @Override
    public PageResult<AttachmentVO> queryList(Long authLetterId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<AuthAttachment> entities = authAttachmentMapper.selectByAuthLetterId(authLetterId, pageSize, offset);
        long total = authAttachmentMapper.countByAuthLetterId(authLetterId);

        List<AttachmentVO> voList = entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public Long create(Long authLetterId, AttachmentVO dto) {
        AuthAttachment entity = new AuthAttachment();
        entity.setAuthLetterId(authLetterId);
        entity.setDocId(dto.getDocId());
        entity.setDocName(dto.getDocName());
        entity.setDocType(dto.getDocType());
        entity.setIsEncrypted(dto.getIsEncrypted() != null ? dto.getIsEncrypted() : false);
        entity.setCreatedBy("system");
        authAttachmentMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        authAttachmentMapper.deleteById(id);
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private AttachmentVO convertToVO(AuthAttachment entity) {
        AttachmentVO vo = new AttachmentVO();
        vo.setId(entity.getId());
        vo.setDocId(entity.getDocId());
        vo.setDocName(entity.getDocName());
        vo.setDocType(entity.getDocType());
        vo.setIsEncrypted(entity.getIsEncrypted());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        return vo;
    }
}