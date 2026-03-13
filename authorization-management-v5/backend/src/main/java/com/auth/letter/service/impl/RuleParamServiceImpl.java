package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthRuleParam;
import com.auth.letter.enums.DataType;
import com.auth.letter.enums.RuleParamStatus;
import com.auth.letter.repository.AuthRuleParamRepository;
import com.auth.letter.service.RuleParamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rule Parameter Service Implementation
 */
@Service
@RequiredArgsConstructor
public class RuleParamServiceImpl implements RuleParamService {

    private final AuthRuleParamRepository ruleParamRepository;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<RuleParamListVO> queryList(RuleParamQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), Sort.by("createdAt").descending());

        Page<AuthRuleParam> page = ruleParamRepository.findByConditions(
                queryDTO.getName(),
                queryDTO.getNameEn(),
                queryDTO.getStatus(),
                pageable);

        List<RuleParamListVO> list = page.getContent().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return new PageResult<>(list, page.getTotalElements(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public RuleParamDTO getById(Long id) {
        AuthRuleParam entity = ruleParamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("规则参数不存在: " + id));

        RuleParamDTO dto = new RuleParamDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNameEn(entity.getNameEn());
        dto.setStatus(entity.getStatus());
        dto.setDataType(entity.getDataType());
        dto.setBusinessMappings(entity.getBusinessMappings());
        return dto;
    }

    @Override
    @Transactional
    public Long create(RuleParamDTO dto) {
        if (ruleParamRepository.existsByNameEn(dto.getNameEn())) {
            throw new RuntimeException("英文名称已存在: " + dto.getNameEn());
        }

        AuthRuleParam entity = new AuthRuleParam();
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : RuleParamStatus.ACTIVE.getCode());
        entity.setDataType(dto.getDataType());
        entity.setBusinessMappings(dto.getBusinessMappings());
        entity.setCreatedBy("system");

        AuthRuleParam saved = ruleParamRepository.save(entity);
        return saved.getId();
    }

    @Override
    @Transactional
    public void update(Long id, RuleParamDTO dto) {
        AuthRuleParam entity = ruleParamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("规则参数不存在: " + id));

        // Check if nameEn is changed and already exists
        if (!entity.getNameEn().equals(dto.getNameEn()) && ruleParamRepository.existsByNameEn(dto.getNameEn())) {
            throw new RuntimeException("英文名称已存在: " + dto.getNameEn());
        }

        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setStatus(dto.getStatus());
        entity.setDataType(dto.getDataType());
        entity.setBusinessMappings(dto.getBusinessMappings());
        entity.setUpdatedBy("system");

        ruleParamRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AuthRuleParam entity = ruleParamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("规则参数不存在: " + id));

        ruleParamRepository.delete(entity);
    }

    @Override
    @Transactional
    public int batchActivate(List<Long> ids) {
        List<AuthRuleParam> params = ruleParamRepository.findAllById(ids);
        for (AuthRuleParam param : params) {
            param.setStatus(RuleParamStatus.ACTIVE.getCode());
            param.setUpdatedBy("system");
        }
        ruleParamRepository.saveAll(params);
        return params.size();
    }

    @Override
    @Transactional
    public int batchDeactivate(List<Long> ids) {
        List<AuthRuleParam> params = ruleParamRepository.findAllById(ids);
        for (AuthRuleParam param : params) {
            param.setStatus(RuleParamStatus.INACTIVE.getCode());
            param.setUpdatedBy("system");
        }
        ruleParamRepository.saveAll(params);
        return params.size();
    }

    @Override
    public List<RuleParamListVO> getAllActive() {
        List<AuthRuleParam> params = ruleParamRepository.findByStatusOrderByCreatedAtDesc(RuleParamStatus.ACTIVE.getCode());
        return params.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    // ==================== Private Methods ====================

    private RuleParamListVO convertToListVO(AuthRuleParam entity) {
        RuleParamListVO vo = new RuleParamListVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setNameEn(entity.getNameEn());
        vo.setStatus(entity.getStatus());
        vo.setStatusText(getStatusText(entity.getStatus()));
        vo.setDataType(entity.getDataType());
        vo.setDataTypeText(getDataTypeText(entity.getDataType()));
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        return vo;
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        try {
            return RuleParamStatus.fromCode(status).getDescription();
        } catch (Exception e) {
            return status;
        }
    }

    private String getDataTypeText(String dataType) {
        if (dataType == null) return "";
        try {
            return DataType.fromCode(dataType).getDescription();
        } catch (Exception e) {
            return dataType;
        }
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATE_FORMATTER);
    }
}