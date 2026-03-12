package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthScene;
import com.auth.letter.mapper.AuthSceneMapper;
import com.auth.letter.service.SceneService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SceneServiceImpl implements SceneService {

    private final AuthSceneMapper authSceneMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<SceneVO> queryList(Long authLetterId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<AuthScene> entities = authSceneMapper.selectByAuthLetterId(authLetterId, pageSize, offset);
        long total = authSceneMapper.countByAuthLetterId(authLetterId);

        List<SceneVO> voList = entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public Long create(Long authLetterId, SceneDTO dto) {
        AuthScene entity = new AuthScene();
        entity.setAuthLetterId(authLetterId);
        entity.setSceneName(dto.getSceneName());
        entity.setIndustry(toJson(dto.getIndustry()));
        entity.setBusinessScenario(dto.getBusinessScenario());
        entity.setDecisionLevel(dto.getDecisionLevel());
        entity.setRuleDetail(dto.getRuleDetail());
        entity.setConditionGroups(toJson(dto.getConditionGroups()));
        entity.setOrderIndex(0);
        entity.setCreatedBy("system");
        authSceneMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, SceneDTO dto) {
        AuthScene entity = authSceneMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("场景不存在");
        }
        entity.setSceneName(dto.getSceneName());
        entity.setIndustry(toJson(dto.getIndustry()));
        entity.setBusinessScenario(dto.getBusinessScenario());
        entity.setDecisionLevel(dto.getDecisionLevel());
        entity.setRuleDetail(dto.getRuleDetail());
        entity.setConditionGroups(toJson(dto.getConditionGroups()));
        entity.setUpdatedBy("system");
        authSceneMapper.update(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        authSceneMapper.deleteById(id);
    }

    @Override
    @Transactional
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return authSceneMapper.batchDelete(ids);
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private SceneVO convertToVO(AuthScene entity) {
        SceneVO vo = new SceneVO();
        vo.setId(entity.getId());
        vo.setSceneName(entity.getSceneName());
        vo.setBusinessScenario(entity.getBusinessScenario());
        vo.setDecisionLevel(entity.getDecisionLevel());
        vo.setRuleDetail(entity.getRuleDetail());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        return vo;
    }
}