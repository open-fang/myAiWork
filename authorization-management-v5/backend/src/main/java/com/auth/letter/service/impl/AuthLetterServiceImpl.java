package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.*;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OperationType;
import com.auth.letter.repository.*;
import com.auth.letter.service.AuthLetterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Authorization Letter Service Implementation
 */
@Service
@RequiredArgsConstructor
public class AuthLetterServiceImpl implements AuthLetterService {

    private final AuthLetterRepository authLetterRepository;
    private final AuthAttachmentRepository attachmentRepository;
    private final AuthSceneRepository sceneRepository;
    private final AuthOperationLogRepository logRepository;
    private final AuthLookupRepository lookupRepository;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<AuthLetterListVO> queryList(AuthLetterQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), Sort.by("createdAt").descending());

        String authTargetLevel = queryDTO.getAuthTargetLevel() != null && !queryDTO.getAuthTargetLevel().isEmpty()
                ? queryDTO.getAuthTargetLevel().get(0) : null;
        String applicableRegion = queryDTO.getApplicableRegion() != null && !queryDTO.getApplicableRegion().isEmpty()
                ? queryDTO.getApplicableRegion().get(0) : null;
        String authPublishLevel = queryDTO.getAuthPublishLevel() != null && !queryDTO.getAuthPublishLevel().isEmpty()
                ? queryDTO.getAuthPublishLevel().get(0) : null;
        String authPublishOrg = queryDTO.getAuthPublishOrg() != null && !queryDTO.getAuthPublishOrg().isEmpty()
                ? queryDTO.getAuthPublishOrg().get(0) : null;

        Page<AuthLetter> page = authLetterRepository.findByConditions(
                queryDTO.getName(),
                queryDTO.getPublishYear(),
                queryDTO.getStatus(),
                authTargetLevel,
                applicableRegion,
                authPublishLevel,
                authPublishOrg,
                pageable);

        List<AuthLetterListVO> list = page.getContent().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return new PageResult<>(list, page.getTotalElements(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public AuthLetterDetailVO getDetail(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在: " + id));

        AuthLetterDetailVO vo = new AuthLetterDetailVO();
        vo.setId(authLetter.getId());
        vo.setCode(authLetter.getCode());
        vo.setName(authLetter.getName());
        vo.setStatus(authLetter.getStatus());
        vo.setStatusText(getStatusText(authLetter.getStatus()));
        vo.setAuthTargetLevel(parseJsonList(authLetter.getAuthTargetLevel()));
        vo.setApplicableRegion(parseJsonList(authLetter.getApplicableRegion()));
        vo.setAuthPublishLevel(parseJsonList(authLetter.getAuthPublishLevel()));
        vo.setAuthPublishOrg(parseJsonList(authLetter.getAuthPublishOrg()));
        vo.setPublishYear(authLetter.getPublishYear());
        vo.setContentSummary(authLetter.getContentSummary());
        vo.setCreatedBy(authLetter.getCreatedBy());
        vo.setCreatedAt(formatDateTime(authLetter.getCreatedAt()));
        vo.setUpdatedBy(authLetter.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(authLetter.getUpdatedAt()));
        vo.setPublishedAt(formatDateTime(authLetter.getPublishedAt()));

        // Load attachments
        List<AuthAttachment> attachments = attachmentRepository.findByAuthLetterIdOrderByCreatedAtDesc(id);
        vo.setAttachments(attachments.stream().map(this::convertToAttachmentVO).collect(Collectors.toList()));

        // Load scenes
        List<AuthScene> scenes = sceneRepository.findByAuthLetterIdOrderByCreatedAtDesc(id);
        vo.setScenes(scenes.stream().map(this::convertToSceneVO).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional
    public Long create(AuthLetterDetailDTO dto) {
        AuthLetter authLetter = new AuthLetter();
        authLetter.setCode(generateCode());
        authLetter.setName(dto.getName());
        authLetter.setStatus(AuthLetterStatus.DRAFT.getCode());
        authLetter.setAuthTargetLevel(toJsonString(dto.getAuthTargetLevel()));
        authLetter.setApplicableRegion(toJsonString(dto.getApplicableRegion()));
        authLetter.setAuthPublishLevel(toJsonString(dto.getAuthPublishLevel()));
        authLetter.setAuthPublishOrg(toJsonString(dto.getAuthPublishOrg()));
        authLetter.setPublishYear(dto.getPublishYear());
        authLetter.setContentSummary(dto.getContentSummary());
        authLetter.setCreatedBy("system");

        AuthLetter saved = authLetterRepository.save(authLetter);

        // Save attachments
        if (dto.getAttachments() != null) {
            for (AttachmentDTO attachmentDTO : dto.getAttachments()) {
                AuthAttachment attachment = new AuthAttachment();
                attachment.setAuthLetterId(saved.getId());
                attachment.setDocId(attachmentDTO.getDocId());
                attachment.setDocName(attachmentDTO.getDocName());
                attachment.setDocType(attachmentDTO.getDocType());
                attachment.setFileSize(attachmentDTO.getFileSize());
                attachment.setEncrypted(attachmentDTO.getEncrypted());
                attachment.setCreatedBy("system");
                attachmentRepository.save(attachment);
            }
        }

        // Save scenes
        if (dto.getScenes() != null) {
            for (SceneDTO sceneDTO : dto.getScenes()) {
                AuthScene scene = new AuthScene();
                scene.setAuthLetterId(saved.getId());
                scene.setName(sceneDTO.getName());
                scene.setIndustry(toJsonString(sceneDTO.getIndustry()));
                scene.setBusinessScenario(sceneDTO.getBusinessScenario());
                scene.setDecisionLevel(sceneDTO.getDecisionLevel());
                scene.setRuleDetail(sceneDTO.getRuleDetail());
                scene.setConditionGroups(sceneDTO.getConditionGroups());
                scene.setCreatedBy("system");
                sceneRepository.save(scene);
            }
        }

        // Save operation log
        saveOperationLog(saved.getId(), OperationType.CREATE, "创建授权书");

        return saved.getId();
    }

    @Override
    @Transactional
    public void update(Long id, AuthLetterDetailDTO dto) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在: " + id));

        if (!AuthLetterStatus.DRAFT.getCode().equals(authLetter.getStatus())) {
            throw new RuntimeException("只有草稿状态的授权书可以编辑");
        }

        authLetter.setName(dto.getName());
        authLetter.setAuthTargetLevel(toJsonString(dto.getAuthTargetLevel()));
        authLetter.setApplicableRegion(toJsonString(dto.getApplicableRegion()));
        authLetter.setAuthPublishLevel(toJsonString(dto.getAuthPublishLevel()));
        authLetter.setAuthPublishOrg(toJsonString(dto.getAuthPublishOrg()));
        authLetter.setPublishYear(dto.getPublishYear());
        authLetter.setContentSummary(dto.getContentSummary());
        authLetter.setUpdatedBy("system");

        authLetterRepository.save(authLetter);

        // Update attachments
        attachmentRepository.deleteByAuthLetterIdAndIdIn(id, new ArrayList<>());
        attachmentRepository.findByAuthLetterIdOrderByCreatedAtDesc(id).forEach(a -> attachmentRepository.delete(a));

        if (dto.getAttachments() != null) {
            for (AttachmentDTO attachmentDTO : dto.getAttachments()) {
                AuthAttachment attachment = new AuthAttachment();
                attachment.setAuthLetterId(id);
                attachment.setDocId(attachmentDTO.getDocId());
                attachment.setDocName(attachmentDTO.getDocName());
                attachment.setDocType(attachmentDTO.getDocType());
                attachment.setFileSize(attachmentDTO.getFileSize());
                attachment.setEncrypted(attachmentDTO.getEncrypted());
                attachment.setCreatedBy("system");
                attachmentRepository.save(attachment);
            }
        }

        // Update scenes
        sceneRepository.findByAuthLetterIdOrderByCreatedAtDesc(id).forEach(s -> sceneRepository.delete(s));

        if (dto.getScenes() != null) {
            for (SceneDTO sceneDTO : dto.getScenes()) {
                AuthScene scene = new AuthScene();
                scene.setAuthLetterId(id);
                scene.setName(sceneDTO.getName());
                scene.setIndustry(toJsonString(sceneDTO.getIndustry()));
                scene.setBusinessScenario(sceneDTO.getBusinessScenario());
                scene.setDecisionLevel(sceneDTO.getDecisionLevel());
                scene.setRuleDetail(sceneDTO.getRuleDetail());
                scene.setConditionGroups(sceneDTO.getConditionGroups());
                scene.setCreatedBy("system");
                sceneRepository.save(scene);
            }
        }

        // Save operation log
        saveOperationLog(id, OperationType.UPDATE, "更新授权书");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在: " + id));

        authLetterRepository.delete(authLetter);
        saveOperationLog(id, OperationType.DELETE, "删除授权书");
    }

    @Override
    @Transactional
    public void publish(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在: " + id));

        if (!AuthLetterStatus.DRAFT.getCode().equals(authLetter.getStatus())) {
            throw new RuntimeException("只有草稿状态的授权书可以发布");
        }

        authLetter.setStatus(AuthLetterStatus.PUBLISHED.getCode());
        authLetter.setPublishedAt(LocalDateTime.now());
        authLetter.setUpdatedBy("system");
        authLetterRepository.save(authLetter);

        saveOperationLog(id, OperationType.PUBLISH, "发布授权书");
    }

    @Override
    @Transactional
    public void expire(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("授权书不存在: " + id));

        if (!AuthLetterStatus.PUBLISHED.getCode().equals(authLetter.getStatus())) {
            throw new RuntimeException("只有已发布状态的授权书可以失效");
        }

        authLetter.setStatus(AuthLetterStatus.EXPIRED.getCode());
        authLetter.setExpiredAt(LocalDateTime.now());
        authLetter.setUpdatedBy("system");
        authLetterRepository.save(authLetter);

        saveOperationLog(id, OperationType.EXPIRE, "授权书失效");
    }

    @Override
    @Transactional
    public int batchPublish(List<Long> ids) {
        List<AuthLetter> letters = authLetterRepository.findByIdsAndStatus(ids, AuthLetterStatus.DRAFT.getCode());
        for (AuthLetter letter : letters) {
            letter.setStatus(AuthLetterStatus.PUBLISHED.getCode());
            letter.setPublishedAt(LocalDateTime.now());
            letter.setUpdatedBy("system");
            saveOperationLog(letter.getId(), OperationType.PUBLISH, "批量发布授权书");
        }
        authLetterRepository.saveAll(letters);
        return letters.size();
    }

    @Override
    @Transactional
    public int batchExpire(List<Long> ids) {
        List<AuthLetter> letters = authLetterRepository.findByIdsAndStatus(ids, AuthLetterStatus.PUBLISHED.getCode());
        for (AuthLetter letter : letters) {
            letter.setStatus(AuthLetterStatus.EXPIRED.getCode());
            letter.setExpiredAt(LocalDateTime.now());
            letter.setUpdatedBy("system");
            saveOperationLog(letter.getId(), OperationType.EXPIRE, "批量失效授权书");
        }
        authLetterRepository.saveAll(letters);
        return letters.size();
    }

    @Override
    @Transactional
    public int batchDelete(List<Long> ids) {
        List<AuthLetter> letters = authLetterRepository.findAllById(ids);
        for (AuthLetter letter : letters) {
            saveOperationLog(letter.getId(), OperationType.DELETE, "批量删除授权书");
        }
        authLetterRepository.deleteAll(letters);
        return letters.size();
    }

    @Override
    public PageResult<OperationLogVO> getOperationLogs(Long authLetterId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("operatedAt").descending());
        Page<AuthOperationLog> page = logRepository.findByAuthLetterIdOrderByOperatedAtDesc(authLetterId, pageable);

        List<OperationLogVO> list = page.getContent().stream()
                .map(this::convertToLogVO)
                .collect(Collectors.toList());

        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    // ==================== Private Methods ====================

    private String generateCode() {
        return "AUTH" + System.currentTimeMillis();
    }

    private AuthLetterListVO convertToListVO(AuthLetter entity) {
        AuthLetterListVO vo = new AuthLetterListVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setStatus(entity.getStatus());
        vo.setStatusText(getStatusText(entity.getStatus()));
        vo.setAuthTargetLevel(entity.getAuthTargetLevel());
        vo.setAuthTargetLevelText(getLookupNames(entity.getAuthTargetLevel(), "AUTH_TARGET_LEVEL"));
        vo.setApplicableRegion(entity.getApplicableRegion());
        vo.setApplicableRegionText(getLookupNames(entity.getApplicableRegion(), "APPLICABLE_REGION"));
        vo.setAuthPublishLevel(entity.getAuthPublishLevel());
        vo.setAuthPublishLevelText(getLookupNames(entity.getAuthPublishLevel(), "AUTH_PUBLISH_LEVEL"));
        vo.setAuthPublishOrg(entity.getAuthPublishOrg());
        vo.setAuthPublishOrgText(getLookupNames(entity.getAuthPublishOrg(), "ORG_TREE"));
        vo.setPublishYear(entity.getPublishYear());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        return vo;
    }

    private AttachmentVO convertToAttachmentVO(AuthAttachment entity) {
        AttachmentVO vo = new AttachmentVO();
        vo.setId(entity.getId());
        vo.setDocId(entity.getDocId());
        vo.setDocName(entity.getDocName());
        vo.setDocType(entity.getDocType());
        vo.setDocTypeText(getLookupName(entity.getDocType(), "DOC_TYPE"));
        vo.setFileSize(entity.getFileSize());
        vo.setEncrypted(entity.getEncrypted());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        return vo;
    }

    private SceneVO convertToSceneVO(AuthScene entity) {
        SceneVO vo = new SceneVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setIndustry(parseJsonList(entity.getIndustry()));
        vo.setIndustryText(getLookupNames(entity.getIndustry(), "INDUSTRY"));
        vo.setBusinessScenario(entity.getBusinessScenario());
        vo.setBusinessScenarioText(getLookupName(entity.getBusinessScenario(), "BUSINESS_SCENARIO"));
        vo.setDecisionLevel(entity.getDecisionLevel());
        vo.setDecisionLevelText(getLookupName(entity.getDecisionLevel(), "DECISION_LEVEL"));
        vo.setRuleDetail(entity.getRuleDetail());
        vo.setConditionGroups(entity.getConditionGroups());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        return vo;
    }

    private OperationLogVO convertToLogVO(AuthOperationLog entity) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(entity.getId());
        vo.setOperationType(entity.getOperationType());
        vo.setOperationTypeText(getOperationTypeText(entity.getOperationType()));
        vo.setOperator(entity.getOperator());
        vo.setOperatedAt(formatDateTime(entity.getOperatedAt()));
        return vo;
    }

    private void saveOperationLog(Long authLetterId, OperationType operationType, String description) {
        AuthOperationLog log = new AuthOperationLog();
        log.setAuthLetterId(authLetterId);
        log.setOperationType(operationType.getCode());
        log.setOperationDesc(description);
        log.setOperator("system");
        logRepository.save(log);
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        try {
            return AuthLetterStatus.fromCode(status).getDescription();
        } catch (Exception e) {
            return status;
        }
    }

    private String getOperationTypeText(String operationType) {
        for (OperationType type : OperationType.values()) {
            if (type.getCode().equals(operationType)) {
                return type.getDescription();
            }
        }
        return operationType;
    }

    private String getLookupName(String code, String lookupType) {
        if (code == null) return "";
        List<AuthLookup> lookups = lookupRepository.findByLookupTypeOrderBySortOrderAsc(lookupType);
        for (AuthLookup lookup : lookups) {
            if (code.equals(lookup.getLookupCode())) {
                return lookup.getLookupName();
            }
        }
        return code;
    }

    private String getLookupNames(String jsonCodes, String lookupType) {
        if (jsonCodes == null || jsonCodes.isEmpty()) return "";

        List<String> codes = parseJsonList(jsonCodes);
        if (codes.isEmpty()) return "";

        Map<String, String> codeToName = new HashMap<>();
        List<AuthLookup> lookups = lookupRepository.findByLookupTypeOrderBySortOrderAsc(lookupType);
        for (AuthLookup lookup : lookups) {
            codeToName.put(lookup.getLookupCode(), lookup.getLookupName());
        }

        return codes.stream()
                .map(code -> codeToName.getOrDefault(code, code))
                .collect(Collectors.joining(", "));
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private String toJsonString(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATE_FORMATTER);
    }
}