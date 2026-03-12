package com.auth.letter.service.impl;

import com.auth.letter.dto.*;
import com.auth.letter.entity.*;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.mapper.*;
import com.auth.letter.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthLetterServiceImpl implements AuthLetterService {

    private final AuthLetterMapper authLetterMapper;
    private final AuthSceneMapper authSceneMapper;
    private final AuthAttachmentMapper authAttachmentMapper;
    private final AuthOperationLogMapper authOperationLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<AuthLetterListVO> queryList(AuthLetterQueryDTO queryDTO) {
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        List<AuthLetter> entities = authLetterMapper.selectByCondition(
                queryDTO.getName(),
                queryDTO.getPublishYear(),
                queryDTO.getStatus(),
                queryDTO.getPageSize(),
                offset
        );
        long total = authLetterMapper.countByCondition(
                queryDTO.getName(),
                queryDTO.getPublishYear(),
                queryDTO.getStatus()
        );

        List<AuthLetterListVO> voList = entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public AuthLetterDetailVO getDetail(Long id) {
        AuthLetter entity = authLetterMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("授权书不存在");
        }
        return convertToDetailVO(entity);
    }

    @Override
    @Transactional
    public Long create(AuthLetterDetailDTO dto) {
        AuthLetter entity = new AuthLetter();
        entity.setCode(generateCode());
        entity.setName(dto.getName());
        entity.setStatus(AuthLetterStatus.DRAFT.name());
        entity.setAuthTargetLevel(toJsonArray(dto.getAuthTargetLevel()));
        entity.setApplicableRegion(toJsonArray(dto.getApplicableRegion()));
        entity.setAuthPublishLevel(toJsonArray(dto.getAuthPublishLevel()));
        entity.setAuthPublishOrg(toJsonArray(dto.getAuthPublishOrg()));
        entity.setPublishYear(dto.getPublishYear());
        entity.setContentSummary(dto.getContentSummary());
        entity.setCreatedBy("system");
        authLetterMapper.insert(entity);

        // 保存场景
        if (dto.getScenes() != null && !dto.getScenes().isEmpty()) {
            int orderIndex = 0;
            for (SceneDTO sceneDTO : dto.getScenes()) {
                AuthScene scene = new AuthScene();
                scene.setAuthLetterId(entity.getId());
                scene.setSceneName(sceneDTO.getSceneName());
                scene.setIndustry(toJsonArray(sceneDTO.getIndustry()));
                scene.setBusinessScenario(sceneDTO.getBusinessScenario());
                scene.setDecisionLevel(sceneDTO.getDecisionLevel());
                scene.setRuleDetail(sceneDTO.getRuleDetail());
                scene.setConditionGroups(toJson(sceneDTO.getConditionGroups()));
                scene.setOrderIndex(orderIndex++);
                scene.setCreatedBy("system");
                authSceneMapper.insert(scene);
            }
        }

        // 记录日志
        saveOperationLog(entity.getId(), "CREATE", "system");

        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, AuthLetterDetailDTO dto) {
        AuthLetter entity = authLetterMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("授权书不存在");
        }
        if (!AuthLetterStatus.DRAFT.name().equals(entity.getStatus())) {
            throw new RuntimeException("只能修改草稿状态的授权书");
        }

        entity.setName(dto.getName());
        entity.setAuthTargetLevel(toJsonArray(dto.getAuthTargetLevel()));
        entity.setApplicableRegion(toJsonArray(dto.getApplicableRegion()));
        entity.setAuthPublishLevel(toJsonArray(dto.getAuthPublishLevel()));
        entity.setAuthPublishOrg(toJsonArray(dto.getAuthPublishOrg()));
        entity.setPublishYear(dto.getPublishYear());
        entity.setContentSummary(dto.getContentSummary());
        entity.setUpdatedBy("system");
        authLetterMapper.update(entity);

        // 删除旧场景，插入新场景
        authSceneMapper.deleteByAuthLetterId(id);
        if (dto.getScenes() != null && !dto.getScenes().isEmpty()) {
            int orderIndex = 0;
            for (SceneDTO sceneDTO : dto.getScenes()) {
                AuthScene scene = new AuthScene();
                scene.setAuthLetterId(id);
                scene.setSceneName(sceneDTO.getSceneName());
                scene.setIndustry(toJsonArray(sceneDTO.getIndustry()));
                scene.setBusinessScenario(sceneDTO.getBusinessScenario());
                scene.setDecisionLevel(sceneDTO.getDecisionLevel());
                scene.setRuleDetail(sceneDTO.getRuleDetail());
                scene.setConditionGroups(toJson(sceneDTO.getConditionGroups()));
                scene.setOrderIndex(orderIndex++);
                scene.setCreatedBy("system");
                authSceneMapper.insert(scene);
            }
        }

        // 记录日志
        saveOperationLog(id, "UPDATE", "system");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AuthLetter entity = authLetterMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("授权书不存在");
        }
        if (!AuthLetterStatus.DRAFT.name().equals(entity.getStatus()) &&
            !AuthLetterStatus.EXPIRED.name().equals(entity.getStatus())) {
            throw new RuntimeException("只能删除草稿或已失效状态的授权书");
        }
        authLetterMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        AuthLetter entity = authLetterMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("授权书不存在");
        }
        if (!AuthLetterStatus.DRAFT.name().equals(entity.getStatus())) {
            throw new RuntimeException("只能发布草稿状态的授权书");
        }
        authLetterMapper.publish(id);

        // 记录日志
        saveOperationLog(id, "PUBLISH", "system");
    }

    @Override
    @Transactional
    public void expire(Long id) {
        AuthLetter entity = authLetterMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("授权书不存在");
        }
        if (!AuthLetterStatus.PUBLISHED.name().equals(entity.getStatus())) {
            throw new RuntimeException("只能失效已发布状态的授权书");
        }
        authLetterMapper.expire(id);

        // 记录日志
        saveOperationLog(id, "EXPIRE", "system");
    }

    @Override
    @Transactional
    public int batchPublish(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = authLetterMapper.batchPublish(ids);
        for (Long id : ids) {
            saveOperationLog(id, "PUBLISH", "system");
        }
        return count;
    }

    @Override
    @Transactional
    public int batchExpire(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = authLetterMapper.batchExpire(ids);
        for (Long id : ids) {
            saveOperationLog(id, "EXPIRE", "system");
        }
        return count;
    }

    @Override
    @Transactional
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return authLetterMapper.batchDelete(ids);
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
                new LookupValue("ORG001", "总部", Arrays.asList(
                        new LookupValue("ORG002", "华东区", Arrays.asList(
                                new LookupValue("ORG003", "上海办事处", Collections.emptyList()),
                                new LookupValue("ORG004", "杭州办事处", Collections.emptyList())
                        )),
                        new LookupValue("ORG005", "华北区", Arrays.asList(
                                new LookupValue("ORG006", "北京办事处", Collections.emptyList()),
                                new LookupValue("ORG007", "天津办事处", Collections.emptyList())
                        ))
                ))
        );
    }

    private void saveOperationLog(Long authLetterId, String operation, String operator) {
        AuthOperationLog log = new AuthOperationLog();
        log.setAuthLetterId(authLetterId);
        log.setOperation(operation);
        log.setOperator(operator);
        authOperationLogMapper.insert(log);
    }

    private String generateCode() {
        return "AUTH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String toJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<String> parseJsonArray(String json) {
        if (!StringUtils.hasText(json)) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private AuthLetterListVO convertToVO(AuthLetter entity) {
        AuthLetterListVO vo = new AuthLetterListVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setStatus(entity.getStatus());
        vo.setStatusText(getStatusText(entity.getStatus()));
        vo.setPublishYear(entity.getPublishYear());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));

        vo.setAuthTargetLevel(parseJsonArray(entity.getAuthTargetLevel()));
        vo.setApplicableRegion(parseJsonArray(entity.getApplicableRegion()));
        vo.setAuthPublishLevel(parseJsonArray(entity.getAuthPublishLevel()));
        vo.setAuthPublishOrg(parseJsonArray(entity.getAuthPublishOrg()));

        vo.setAuthTargetLevelText(convertCodesToText(vo.getAuthTargetLevel(), "AUTH_TARGET_LEVEL"));
        vo.setApplicableRegionText(convertCodesToText(vo.getApplicableRegion(), "APPLICABLE_REGION"));
        vo.setAuthPublishLevelText(convertCodesToText(vo.getAuthPublishLevel(), "AUTH_PUBLISH_LEVEL"));
        vo.setAuthPublishOrgText(convertCodesToText(vo.getAuthPublishOrg(), "AUTH_PUBLISH_ORG"));

        return vo;
    }

    private AuthLetterDetailVO convertToDetailVO(AuthLetter entity) {
        AuthLetterDetailVO vo = new AuthLetterDetailVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setStatus(entity.getStatus());
        vo.setStatusText(getStatusText(entity.getStatus()));
        vo.setPublishYear(entity.getPublishYear());
        vo.setContentSummary(entity.getContentSummary());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedAt(formatDateTime(entity.getCreatedAt()));
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(entity.getUpdatedAt()));
        vo.setPublishedAt(formatDateTime(entity.getPublishedAt()));

        vo.setAuthTargetLevel(parseJsonArray(entity.getAuthTargetLevel()));
        vo.setApplicableRegion(parseJsonArray(entity.getApplicableRegion()));
        vo.setAuthPublishLevel(parseJsonArray(entity.getAuthPublishLevel()));
        vo.setAuthPublishOrg(parseJsonArray(entity.getAuthPublishOrg()));

        vo.setAuthTargetLevelText(convertCodesToText(vo.getAuthTargetLevel(), "AUTH_TARGET_LEVEL"));
        vo.setApplicableRegionText(convertCodesToText(vo.getApplicableRegion(), "APPLICABLE_REGION"));
        vo.setAuthPublishLevelText(convertCodesToText(vo.getAuthPublishLevel(), "AUTH_PUBLISH_LEVEL"));
        vo.setAuthPublishOrgText(convertCodesToText(vo.getAuthPublishOrg(), "AUTH_PUBLISH_ORG"));

        // 加载场景
        List<SceneVO> scenes = new ArrayList<>();
        List<AuthScene> sceneEntities = authSceneMapper.selectByAuthLetterId(entity.getId(), 100, 0);
        for (AuthScene scene : sceneEntities) {
            scenes.add(convertToSceneVO(scene));
        }
        vo.setScenes(scenes);

        // 加载附件
        List<AttachmentVO> attachments = new ArrayList<>();
        List<AuthAttachment> attachmentEntities = authAttachmentMapper.selectByAuthLetterId(entity.getId(), 100, 0);
        for (AuthAttachment attachment : attachmentEntities) {
            attachments.add(convertToAttachmentVO(attachment));
        }
        vo.setAttachments(attachments);

        // 加载日志
        List<OperationLogVO> logs = new ArrayList<>();
        List<AuthOperationLog> logEntities = authOperationLogMapper.selectByAuthLetterId(entity.getId(), 100, 0);
        for (AuthOperationLog log : logEntities) {
            logs.add(convertToLogVO(log));
        }
        vo.setLogs(logs);

        return vo;
    }

    private SceneVO convertToSceneVO(AuthScene scene) {
        SceneVO vo = new SceneVO();
        vo.setId(scene.getId());
        vo.setSceneName(scene.getSceneName());
        vo.setIndustry(parseJsonArray(scene.getIndustry()));
        vo.setIndustryText(convertCodesToText(vo.getIndustry(), "INDUSTRY"));
        vo.setBusinessScenario(scene.getBusinessScenario());
        vo.setDecisionLevel(scene.getDecisionLevel());
        vo.setRuleDetail(scene.getRuleDetail());
        vo.setCreatedBy(scene.getCreatedBy());
        vo.setCreatedAt(formatDateTime(scene.getCreatedAt()));
        vo.setUpdatedBy(scene.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(scene.getUpdatedAt()));
        return vo;
    }

    private AttachmentVO convertToAttachmentVO(AuthAttachment attachment) {
        AttachmentVO vo = new AttachmentVO();
        vo.setId(attachment.getId());
        vo.setDocId(attachment.getDocId());
        vo.setDocName(attachment.getDocName());
        vo.setDocType(attachment.getDocType());
        vo.setIsEncrypted(attachment.getIsEncrypted());
        vo.setCreatedBy(attachment.getCreatedBy());
        vo.setCreatedAt(formatDateTime(attachment.getCreatedAt()));
        vo.setUpdatedBy(attachment.getUpdatedBy());
        vo.setUpdatedAt(formatDateTime(attachment.getUpdatedAt()));
        return vo;
    }

    private OperationLogVO convertToLogVO(AuthOperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(log.getId());
        vo.setOperation(log.getOperation());
        vo.setOperationText(getOperationText(log.getOperation()));
        vo.setOperator(log.getOperator());
        vo.setOperatedAt(formatDateTime(log.getOperatedAt()));
        return vo;
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "DRAFT": return "草稿";
            case "PUBLISHED": return "已发布";
            case "EXPIRED": return "已失效";
            default: return status;
        }
    }

    private String getOperationText(String operation) {
        if (operation == null) return "";
        switch (operation) {
            case "CREATE": return "创建";
            case "UPDATE": return "更新";
            case "PUBLISH": return "发布";
            case "EXPIRE": return "失效";
            case "DELETE": return "删除";
            default: return operation;
        }
    }

    private String convertCodesToText(List<String> codes, String lookupCode) {
        if (codes == null || codes.isEmpty()) return "";
        List<LookupValue> lookupValues = getLookupValues(lookupCode);
        return codes.stream()
                .map(code -> lookupValues.stream()
                        .filter(lv -> lv.getCode().equals(code))
                        .map(LookupValue::getName)
                        .findFirst()
                        .orElse(code))
                .collect(Collectors.joining("、"));
    }
}