package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.RuleParam;
import com.auth.letter.repository.RuleParamRepository;
import com.auth.letter.service.RuleParamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则参数服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleParamServiceImpl implements RuleParamService {

    private final RuleParamRepository ruleParamRepository;
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<RuleParamListVO> queryList(RuleParamQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(
                queryDTO.getPageNum() - 1,
                queryDTO.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<RuleParam> spec = (root, query, cb) -> {
            Predicate predicates = cb.conjunction();

            if (StringUtils.hasText(queryDTO.getName())) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("name")), "%" + queryDTO.getName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(queryDTO.getNameEn())) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("nameEn")), "%" + queryDTO.getNameEn().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(queryDTO.getStatus())) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("status"), queryDTO.getStatus()));
            }

            return predicates;
        };

        Page<RuleParam> page = ruleParamRepository.findAll(spec, pageable);

        List<RuleParamListVO> voList = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotalElements(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public RuleParamListVO getDetail(Long id) {
        return ruleParamRepository.findById(id)
                .map(this::convertToVO)
                .orElse(null);
    }

    @Override
    @Transactional
    public Long create(RuleParamDTO dto) {
        RuleParam entity = new RuleParam();
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setStatus(dto.getStatus());
        entity.setDataType(dto.getDataType());
        entity.setBusinessMappings(serializeBusinessMappings(dto.getBusinessMappings()));
        entity.setCreatedBy("system"); // TODO: Get from security context

        RuleParam saved = ruleParamRepository.save(entity);
        return saved.getId();
    }

    @Override
    @Transactional
    public void update(Long id, RuleParamDTO dto) {
        RuleParam entity = ruleParamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("规则参数不存在: " + id));

        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setStatus(dto.getStatus());
        entity.setDataType(dto.getDataType());
        entity.setBusinessMappings(serializeBusinessMappings(dto.getBusinessMappings()));
        entity.setUpdatedBy("system"); // TODO: Get from security context

        ruleParamRepository.save(entity);
    }

    @Override
    @Transactional
    public int batchActivate(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return ruleParamRepository.batchActivate(ids);
    }

    @Override
    @Transactional
    public int batchDeactivate(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return ruleParamRepository.batchDeactivate(ids);
    }

    private RuleParamListVO convertToVO(RuleParam entity) {
        RuleParamListVO vo = new RuleParamListVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setNameEn(entity.getNameEn());
        vo.setStatus(entity.getStatus());
        vo.setDataType(entity.getDataType());
        vo.setBusinessMappings(deserializeBusinessMappings(entity.getBusinessMappings()));
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setUpdatedBy(entity.getUpdatedBy());

        if (entity.getCreatedAt() != null) {
            vo.setCreatedAt(entity.getCreatedAt().format(formatter));
        }
        if (entity.getUpdatedAt() != null) {
            vo.setUpdatedAt(entity.getUpdatedAt().format(formatter));
        }

        return vo;
    }

    private String serializeBusinessMappings(List<RuleParamDTO.BusinessMapping> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(mappings);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize business mappings", e);
            return null;
        }
    }

    private List<RuleParamDTO.BusinessMapping> deserializeBusinessMappings(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<RuleParamDTO.BusinessMapping>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize business mappings: {}", json, e);
            return Collections.emptyList();
        }
    }
}