package com.auth.management.service.impl;

import com.auth.management.dto.request.BatchAuthLetterRequest;
import com.auth.management.dto.request.CreateAuthLetterRequest;
import com.auth.management.dto.response.*;
import com.auth.management.entity.AuthLetter;
import com.auth.management.entity.AuthLetterLog;
import com.auth.management.entity.AuthLetterScene;
import com.auth.management.enums.AuthLetterStatus;
import com.auth.management.enums.OperationType;
import com.auth.management.exception.BusinessException;
import com.auth.management.exception.ErrorCode;
import com.auth.management.repository.AuthLetterRepository;
import com.auth.management.repository.AuthLetterSceneRepository;
import com.auth.management.service.AuthLetterService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Authorization Letter Service Implementation
 */
@Service
public class AuthLetterServiceImpl implements AuthLetterService {

    @Autowired
    private AuthLetterRepository authLetterRepository;

    @Autowired
    private AuthLetterSceneRepository authLetterSceneRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResponse<AuthLetterListResponse> queryList(String name, String authObjectLevel,
                                                           String applicableRegion, String authPublishLevel,
                                                           String authPublishOrg, Integer publishYear,
                                                           String status, Integer pageNum, Integer pageSize) {
        Page<AuthLetter> page = new Page<>(pageNum, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("authObjectLevel", authObjectLevel);
        params.put("applicableRegion", applicableRegion);
        params.put("authPublishLevel", authPublishLevel);
        params.put("authPublishOrg", authPublishOrg);
        params.put("publishYear", publishYear);
        params.put("status", status);

        Page<AuthLetter> result = authLetterRepository.findPage(page, params);

        List<AuthLetterListResponse> list = result.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());

        return PageResponse.of(list, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public AuthLetterDetailResponse getDetail(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id);
        if (authLetter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Authorization letter not found");
        }
        return convertToDetailResponse(authLetter);
    }

    @Override
    @Transactional
    public Long create(CreateAuthLetterRequest request) {
        // Check name uniqueness
        if (authLetterRepository.checkNameExists(request.getName(), null)) {
            throw new BusinessException(ErrorCode.AUTH_LETTER_NAME_EXISTS,
                    "Authorization letter name already exists: " + request.getName());
        }

        AuthLetter authLetter = new AuthLetter();
        authLetter.setName(request.getName());
        authLetter.setAuthObjectLevel(toJsonString(request.getAuthObjectLevel()));
        authLetter.setApplicableRegion(toJsonString(request.getApplicableRegion()));
        authLetter.setAuthPublishLevel(toJsonString(request.getAuthPublishLevel()));
        authLetter.setAuthPublishOrg(toJsonString(request.getAuthPublishOrg()));
        authLetter.setPublishYear(request.getPublishYear());
        authLetter.setSummary(request.getSummary());
        authLetter.setStatus(AuthLetterStatus.DRAFT.getCode());

        Long id = authLetterRepository.insert(authLetter);

        // Log operation
        logOperation(id, OperationType.CREATE, "system", "System");

        return id;
    }

    @Override
    @Transactional
    public void update(Long id, CreateAuthLetterRequest request) {
        AuthLetter authLetter = authLetterRepository.findById(id);
        if (authLetter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Authorization letter not found");
        }

        // Check status
        if (!AuthLetterStatus.DRAFT.getCode().equals(authLetter.getStatus())) {
            throw new BusinessException(ErrorCode.AUTH_LETTER_STATUS_INVALID,
                    "Only DRAFT status can be updated");
        }

        // Check name uniqueness
        if (authLetterRepository.checkNameExists(request.getName(), id)) {
            throw new BusinessException(ErrorCode.AUTH_LETTER_NAME_EXISTS,
                    "Authorization letter name already exists: " + request.getName());
        }

        authLetter.setName(request.getName());
        authLetter.setAuthObjectLevel(toJsonString(request.getAuthObjectLevel()));
        authLetter.setApplicableRegion(toJsonString(request.getApplicableRegion()));
        authLetter.setAuthPublishLevel(toJsonString(request.getAuthPublishLevel()));
        authLetter.setAuthPublishOrg(toJsonString(request.getAuthPublishOrg()));
        authLetter.setPublishYear(request.getPublishYear());
        authLetter.setSummary(request.getSummary());

        authLetterRepository.update(authLetter);

        // Log operation
        logOperation(id, OperationType.UPDATE, "system", "System");
    }

    private String toJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    @Transactional
    public void publish(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id);
        if (authLetter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Authorization letter not found");
        }

        // Check status
        if (!AuthLetterStatus.DRAFT.getCode().equals(authLetter.getStatus())) {
            throw new BusinessException(ErrorCode.AUTH_LETTER_STATUS_INVALID,
                    "Only DRAFT status can be published");
        }

        // Check if has scenes
        List<AuthLetterScene> scenes = authLetterSceneRepository.findByAuthLetterId(id);
        if (scenes.isEmpty()) {
            throw new BusinessException(ErrorCode.AUTH_LETTER_INCOMPLETE,
                    "At least one scene must be configured before publishing");
        }

        authLetter.setStatus(AuthLetterStatus.PUBLISHED.getCode());
        authLetterRepository.update(authLetter);

        // Log operation
        logOperation(id, OperationType.PUBLISH, "system", "System");
    }

    @Override
    @Transactional
    public void invalidate(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id);
        if (authLetter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Authorization letter not found");
        }

        // Check status
        if (!AuthLetterStatus.PUBLISHED.getCode().equals(authLetter.getStatus())) {
            throw new BusinessException(ErrorCode.AUTH_LETTER_STATUS_INVALID,
                    "Only PUBLISHED status can be invalidated");
        }

        authLetter.setStatus(AuthLetterStatus.INVALID.getCode());
        authLetterRepository.update(authLetter);

        // Log operation
        logOperation(id, OperationType.INVALIDATE, "system", "System");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id);
        if (authLetter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Authorization letter not found");
        }

        authLetterRepository.delete(id);

        // Log operation
        logOperation(id, OperationType.DELETE, "system", "System");
    }

    @Override
    @Transactional
    public BatchOperationResponse batchOperation(BatchAuthLetterRequest request) {
        BatchOperationResponse response = new BatchOperationResponse();
        response.setSuccessCount(0);
        response.setFailCount(0);
        response.setFailDetails(new ArrayList<>());

        for (Long id : request.getIds()) {
            try {
                switch (request.getOperation()) {
                    case "PUBLISH":
                        publish(id);
                        break;
                    case "INVALIDATE":
                        invalidate(id);
                        break;
                    case "DELETE":
                        delete(id);
                        break;
                    default:
                        throw new BusinessException(ErrorCode.PARAM_ERROR, "Invalid operation: " + request.getOperation());
                }
                response.setSuccessCount(response.getSuccessCount() + 1);
            } catch (BusinessException e) {
                response.setFailCount(response.getFailCount() + 1);
                BatchOperationResponse.FailDetail detail = new BatchOperationResponse.FailDetail();
                detail.setId(id);
                detail.setReason(e.getMessage());
                response.getFailDetails().add(detail);
            }
        }

        return response;
    }

    @Override
    public PageResponse<OperationLogResponse> queryLogs(Long authLetterId, Integer pageNum, Integer pageSize) {
        Page<AuthLetterLog> page = new Page<>(pageNum, pageSize);
        Page<AuthLetterLog> result = authLetterRepository.findLogPage(page, authLetterId);

        List<OperationLogResponse> list = result.getRecords().stream()
                .map(this::convertToLogResponse)
                .collect(Collectors.toList());

        return PageResponse.of(list, result.getTotal(), pageNum, pageSize);
    }

    private void logOperation(Long authLetterId, OperationType operationType, String operator, String operatorName) {
        AuthLetterLog log = new AuthLetterLog();
        log.setAuthLetterId(authLetterId);
        log.setOperation(operationType.getCode());
        log.setOperator(operator);
        log.setOperatorName(operatorName);
        authLetterRepository.insertLog(log);
    }

    private AuthLetterListResponse convertToListResponse(AuthLetter entity) {
        AuthLetterListResponse response = new AuthLetterListResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setStatus(entity.getStatus());
        response.setStatusName(getStatusName(entity.getStatus()));
        response.setPublishYear(entity.getPublishYear());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedTime(entity.getCreatedTime());

        // Parse JSON arrays and convert to display names
        response.setAuthObjectLevel(convertJsonToDisplayNames(entity.getAuthObjectLevel(), "AUTH_OBJECT_LEVEL"));
        response.setApplicableRegion(convertJsonToDisplayNames(entity.getApplicableRegion(), "APPLICABLE_REGION"));
        response.setAuthPublishLevel(convertJsonToDisplayNames(entity.getAuthPublishLevel(), "AUTH_PUBLISH_LEVEL"));
        response.setAuthPublishOrg(convertJsonToDisplayNames(entity.getAuthPublishOrg(), "AUTH_PUBLISH_ORG"));

        return response;
    }

    private AuthLetterDetailResponse convertToDetailResponse(AuthLetter entity) {
        AuthLetterDetailResponse response = new AuthLetterDetailResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setPublishYear(entity.getPublishYear());
        response.setSummary(entity.getSummary());
        response.setStatus(entity.getStatus());
        response.setStatusName(getStatusName(entity.getStatus()));
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedTime(entity.getCreatedTime());
        response.setUpdatedBy(entity.getUpdatedBy());
        response.setUpdatedTime(entity.getUpdatedTime());

        // Parse JSON arrays
        response.setAuthObjectLevel(parseJsonArray(entity.getAuthObjectLevel()));
        response.setAuthObjectLevelNames(getNamesFromCodes(parseJsonArray(entity.getAuthObjectLevel()), "AUTH_OBJECT_LEVEL"));
        response.setApplicableRegion(parseJsonArray(entity.getApplicableRegion()));
        response.setApplicableRegionNames(getNamesFromCodes(parseJsonArray(entity.getApplicableRegion()), "APPLICABLE_REGION"));
        response.setAuthPublishLevel(parseJsonArray(entity.getAuthPublishLevel()));
        response.setAuthPublishLevelNames(getNamesFromCodes(parseJsonArray(entity.getAuthPublishLevel()), "AUTH_PUBLISH_LEVEL"));
        response.setAuthPublishOrg(parseJsonArray(entity.getAuthPublishOrg()));
        response.setAuthPublishOrgNames(getNamesFromCodes(parseJsonArray(entity.getAuthPublishOrg()), "AUTH_PUBLISH_ORG"));

        return response;
    }

    private OperationLogResponse convertToLogResponse(AuthLetterLog log) {
        OperationLogResponse response = new OperationLogResponse();
        response.setId(log.getId());
        response.setOperation(log.getOperation());
        response.setOperationName(getOperationName(log.getOperation()));
        response.setOperator(log.getOperator());
        response.setOperatorName(log.getOperatorName());
        response.setOperationTime(log.getOperationTime());
        return response;
    }

    private String getStatusName(String statusCode) {
        AuthLetterStatus status = AuthLetterStatus.fromCode(statusCode);
        return status != null ? status.getName() : statusCode;
    }

    private String getOperationName(String operationCode) {
        OperationType operation = OperationType.fromCode(operationCode);
        return operation != null ? operation.getName() : operationCode;
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String convertJsonToDisplayNames(String json, String lookupType) {
        List<String> codes = parseJsonArray(json);
        if (codes.isEmpty()) {
            return "";
        }
        return String.join(",", codes);
    }

    private List<String> getNamesFromCodes(List<String> codes, String lookupType) {
        // For simplicity, return codes as names
        // In real implementation, should lookup from database
        return codes;
    }
}