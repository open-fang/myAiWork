package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.service.AuthLetterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Authorization Letter Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthLetterServiceImpl implements AuthLetterService {

    private final AuthLetterRepository authLetterRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<AuthLetterListVO> queryList(AuthLetterQueryDTO queryDTO) {
        // Build pageable with sorting
        Pageable pageable = PageRequest.of(
                queryDTO.getPageNum() - 1,
                queryDTO.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Build specification for filtering
        Specification<AuthLetter> spec = (root, query, cb) -> {
            Predicate predicates = cb.conjunction();

            if (StringUtils.hasText(queryDTO.getName())) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("name")), "%" + queryDTO.getName().toLowerCase() + "%"));
            }

            if (queryDTO.getPublishYear() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("publishYear"), queryDTO.getPublishYear()));
            }

            if (StringUtils.hasText(queryDTO.getStatus())) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("status"), queryDTO.getStatus()));
            }

            return predicates;
        };

        Page<AuthLetter> page = authLetterRepository.findAll(spec, pageable);

        // Convert to VO
        List<AuthLetterListVO> voList = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotalElements(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public AuthLetterDetailVO getDetail(Long id) {
        AuthLetter entity = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在"));
        return convertToDetailVO(entity);
    }

    @Override
    @Transactional
    public Long create(AuthLetterDetailDTO dto) {
        AuthLetter entity = new AuthLetter();
        entity.setCode(generateCode());
        entity.setName(dto.getName());
        entity.setStatus(AuthLetterStatus.DRAFT);
        entity.setAuthPublishLevel(toJsonArray(dto.getAuthPublishLevel()));
        entity.setAuthPublishOrg(toJsonArray(dto.getAuthPublishOrg()));
        entity.setAuthTargetLevel(toJsonArray(dto.getAuthTargetLevel()));
        entity.setApplicableRegion(toJsonArray(dto.getApplicableRegion()));
        entity.setPublishYear(dto.getPublishYear());
        entity.setContentSummary(dto.getContentSummary());
        entity.setCreatedBy("system");
        entity = authLetterRepository.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, AuthLetterDetailDTO dto) {
        AuthLetter entity = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在"));

        if (entity.getStatus() != AuthLetterStatus.DRAFT) {
            throw new RuntimeException("只能修改草稿状态的授权书");
        }

        entity.setName(dto.getName());
        entity.setAuthPublishLevel(toJsonArray(dto.getAuthPublishLevel()));
        entity.setAuthPublishOrg(toJsonArray(dto.getAuthPublishOrg()));
        entity.setAuthTargetLevel(toJsonArray(dto.getAuthTargetLevel()));
        entity.setApplicableRegion(toJsonArray(dto.getApplicableRegion()));
        entity.setPublishYear(dto.getPublishYear());
        entity.setContentSummary(dto.getContentSummary());
        authLetterRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AuthLetter entity = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在"));

        if (entity.getStatus() != AuthLetterStatus.DRAFT) {
            throw new RuntimeException("只能删除草稿状态的授权书");
        }

        authLetterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        AuthLetter entity = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在"));

        if (entity.getStatus() != AuthLetterStatus.DRAFT) {
            throw new RuntimeException("只能发布草稿状态的授权书");
        }

        entity.setStatus(AuthLetterStatus.PUBLISHED);
        entity.setPublishedAt(LocalDateTime.now());
        authLetterRepository.save(entity);
    }

    private String generateCode() {
        return "AUTH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String toJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert list to JSON array", e);
            return null;
        }
    }

    private AuthLetterDetailVO convertToDetailVO(AuthLetter entity) {
        AuthLetterDetailVO vo = new AuthLetterDetailVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setStatus(entity.getStatus().name());
        vo.setStatusText(entity.getStatus().getDescription());
        vo.setAuthPublishLevel(parseJsonArray(entity.getAuthPublishLevel()));
        vo.setAuthPublishLevelText(convertCodesToText(vo.getAuthPublishLevel(), "AUTH_PUBLISH_LEVEL"));
        vo.setAuthPublishOrg(parseJsonArray(entity.getAuthPublishOrg()));
        vo.setAuthPublishOrgText(convertCodesToText(vo.getAuthPublishOrg(), "AUTH_PUBLISH_ORG"));
        vo.setAuthTargetLevel(parseJsonArray(entity.getAuthTargetLevel()));
        vo.setAuthTargetLevelText(convertCodesToText(vo.getAuthTargetLevel(), "AUTH_TARGET_LEVEL"));
        vo.setApplicableRegion(parseJsonArray(entity.getApplicableRegion()));
        vo.setApplicableRegionText(convertCodesToText(vo.getApplicableRegion(), "APPLICABLE_REGION"));
        vo.setPublishYear(entity.getPublishYear());
        vo.setContentSummary(entity.getContentSummary());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        vo.setPublishedAt(formatDateTime(entity.getPublishedAt()));

        // Convert scenes
        List<SceneVO> scenes = new ArrayList<>();
        if (entity.getScenes() != null) {
            for (Scene scene : entity.getScenes()) {
                scenes.add(convertToSceneVO(scene));
            }
        }
        vo.setScenes(scenes);

        return vo;
    }

    private SceneVO convertToSceneVO(Scene scene) {
        SceneVO vo = new SceneVO();
        vo.setId(scene.getId());
        vo.setSceneName(scene.getName());
        vo.setRuleDetail(scene.getDescription());
        vo.setCreatedBy(scene.getAuthLetter() != null ? scene.getAuthLetter().getCreatedBy() : null);
        vo.setCreatedAt(formatDateTime(scene.getCreatedAt()));
        vo.setUpdatedBy(null);
        vo.setUpdatedAt(formatDateTime(scene.getUpdatedAt()));
        vo.setConditions(new ArrayList<>());
        return vo;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    @Transactional
    public int batchPublish(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return authLetterRepository.batchPublish(ids);
    }

    @Override
    @Transactional
    public int batchExpire(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return authLetterRepository.batchExpire(ids);
    }

    @Override
    @Transactional
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return authLetterRepository.batchDelete(ids);
    }

    @Override
    public List<LookupValue> getLookupValues(String code) {
        switch (code) {
            case "AUTH_TARGET_LEVEL":
            case "AUTH_PUBLISH_LEVEL":
                return Arrays.asList(
                        new LookupValue("ORGANIZATION", "机关", null),
                        new LookupValue("REGIONAL_DEPT", "地区部", null),
                        new LookupValue("REPRESENTATIVE_OFFICE", "代表处", null),
                        new LookupValue("OFFICE", "办事处", null)
                );
            case "APPLICABLE_REGION":
                return Arrays.asList(
                        new LookupValue("EAST", "华东", null),
                        new LookupValue("NORTH", "华北", null),
                        new LookupValue("SOUTH", "华南", null),
                        new LookupValue("WEST", "西部", null),
                        new LookupValue("CENTRAL", "华中", null)
                );
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public Object getOrgTree() {
        return Arrays.asList(
                new OrgTreeNode("ORG001", "总部", null, Arrays.asList(
                        new OrgTreeNode("ORG002", "华东区", "ORG001", Arrays.asList(
                                new OrgTreeNode("ORG003", "上海办事处", "ORG002", Collections.emptyList()),
                                new OrgTreeNode("ORG004", "杭州办事处", "ORG002", Collections.emptyList())
                        )),
                        new OrgTreeNode("ORG005", "华北区", "ORG001", Arrays.asList(
                                new OrgTreeNode("ORG006", "北京办事处", "ORG005", Collections.emptyList()),
                                new OrgTreeNode("ORG007", "天津办事处", "ORG005", Collections.emptyList())
                        ))
                ))
        );
    }

    private AuthLetterListVO convertToVO(AuthLetter entity) {
        AuthLetterListVO vo = new AuthLetterListVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setStatus(entity.getStatus().name());
        vo.setStatusText(entity.getStatus().getDescription());
        vo.setPublishYear(entity.getPublishYear());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(entity.getCreatedAt());

        // Parse JSON arrays
        vo.setAuthTargetLevel(parseJsonArray(entity.getAuthTargetLevel()));
        vo.setApplicableRegion(parseJsonArray(entity.getApplicableRegion()));
        vo.setAuthPublishLevel(parseJsonArray(entity.getAuthPublishLevel()));
        vo.setAuthPublishOrg(parseJsonArray(entity.getAuthPublishOrg()));

        // Convert codes to display text
        vo.setAuthTargetLevelText(convertCodesToText(vo.getAuthTargetLevel(), "AUTH_TARGET_LEVEL"));
        vo.setApplicableRegionText(convertCodesToText(vo.getApplicableRegion(), "APPLICABLE_REGION"));
        vo.setAuthPublishLevelText(convertCodesToText(vo.getAuthPublishLevel(), "AUTH_PUBLISH_LEVEL"));
        vo.setAuthPublishOrgText(convertCodesToText(vo.getAuthPublishOrg(), "AUTH_PUBLISH_ORG"));

        return vo;
    }

    private List<String> parseJsonArray(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON array: {}", json, e);
            return Collections.emptyList();
        }
    }

    private List<String> convertCodesToText(List<String> codes, String lookupCode) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        List<LookupValue> lookupValues = getLookupValues(lookupCode);
        return codes.stream()
                .map(code -> lookupValues.stream()
                        .filter(lv -> lv.getCode().equals(code))
                        .map(LookupValue::getName)
                        .findFirst()
                        .orElse(code))
                .collect(Collectors.toList());
    }

    /**
     * Organization Tree Node for mock data
     */
    @Data
    public static class OrgTreeNode {
        private String code;
        private String name;
        private String parentCode;
        private List<OrgTreeNode> children;

        public OrgTreeNode(String code, String name, String parentCode, List<OrgTreeNode> children) {
            this.code = code;
            this.name = name;
            this.parentCode = parentCode;
            this.children = children;
        }
    }
}