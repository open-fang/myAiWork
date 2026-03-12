package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.RuleParam;
import com.auth.letter.mapper.RuleParamMapper;
import com.auth.letter.service.RuleParamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleParamServiceImpl implements RuleParamService {

    private final RuleParamMapper ruleParamMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<RuleParamListVO> queryList(RuleParamQueryDTO queryDTO) {
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        List<RuleParam> entities = ruleParamMapper.selectByCondition(
                queryDTO.getName(),
                queryDTO.getNameEn(),
                queryDTO.getStatus(),
                queryDTO.getPageSize(),
                offset
        );
        long total = ruleParamMapper.countByCondition(
                queryDTO.getName(),
                queryDTO.getNameEn(),
                queryDTO.getStatus()
        );

        List<RuleParamListVO> voList = entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public RuleParamDTO getDetail(Long id) {
        RuleParam entity = ruleParamMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("规则参数不存在");
        }
        return convertToDTO(entity);
    }

    @Override
    @Transactional
    public Long create(RuleParamDTO dto) {
        RuleParam entity = new RuleParam();
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        entity.setDataType(dto.getDataType());
        entity.setBusinessMappings(toJson(dto.getBusinessMappings()));
        entity.setCreatedBy("system");
        ruleParamMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, RuleParamDTO dto) {
        RuleParam entity = ruleParamMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("规则参数不存在");
        }
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setStatus(dto.getStatus());
        entity.setDataType(dto.getDataType());
        entity.setBusinessMappings(toJson(dto.getBusinessMappings()));
        entity.setUpdatedBy("system");
        ruleParamMapper.update(entity);
    }

    @Override
    @Transactional
    public int batchActivate(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return ruleParamMapper.batchActivate(ids);
    }

    @Override
    @Transactional
    public int batchDeactivate(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return ruleParamMapper.batchDeactivate(ids);
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<RuleParamDTO.BusinessMapping> parseBusinessMappings(String json) {
        if (!StringUtils.hasText(json)) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<RuleParamDTO.BusinessMapping>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private RuleParamListVO convertToVO(RuleParam entity) {
        RuleParamListVO vo = new RuleParamListVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setNameEn(entity.getNameEn());
        vo.setStatus(entity.getStatus());
        vo.setStatusText(getStatusText(entity.getStatus()));
        vo.setDataType(entity.getDataType());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        return vo;
    }

    private RuleParamDTO convertToDTO(RuleParam entity) {
        RuleParamDTO dto = new RuleParamDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNameEn(entity.getNameEn());
        dto.setStatus(entity.getStatus());
        dto.setDataType(entity.getDataType());
        dto.setBusinessMappings(parseBusinessMappings(entity.getBusinessMappings()));
        return dto;
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "ACTIVE": return "生效";
            case "INACTIVE": return "失效";
            default: return status;
        }
    }
}