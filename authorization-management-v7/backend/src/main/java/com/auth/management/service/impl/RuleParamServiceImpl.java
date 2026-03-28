package com.auth.management.service.impl;

import com.auth.management.dto.request.BatchRuleParamStatusRequest;
import com.auth.management.dto.request.CreateRuleParamRequest;
import com.auth.management.dto.response.PageResponse;
import com.auth.management.dto.response.RuleParamDetailResponse;
import com.auth.management.dto.response.RuleParamListResponse;
import com.auth.management.dto.response.RuleParamOptionResponse;
import com.auth.management.entity.RuleParam;
import com.auth.management.enums.DataType;
import com.auth.management.enums.RuleParamStatus;
import com.auth.management.exception.BusinessException;
import com.auth.management.exception.ErrorCode;
import com.auth.management.repository.RuleParamRepository;
import com.auth.management.service.RuleParamService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Rule Parameter Service Implementation
 */
@Service
public class RuleParamServiceImpl implements RuleParamService {

    @Autowired
    private RuleParamRepository ruleParamRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResponse<RuleParamListResponse> queryList(String name, String nameEn, String status,
                                                          Integer pageNum, Integer pageSize) {
        Page<RuleParam> page = new Page<>(pageNum, pageSize);
        Page<RuleParam> result = ruleParamRepository.findPage(page, name, nameEn, status);

        List<RuleParamListResponse> list = result.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());

        return PageResponse.of(list, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public RuleParamDetailResponse getDetail(Long id) {
        RuleParam ruleParam = ruleParamRepository.findById(id);
        if (ruleParam == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Rule parameter not found");
        }
        return convertToDetailResponse(ruleParam);
    }

    @Override
    public Long create(CreateRuleParamRequest request) {
        // Check name uniqueness
        if (ruleParamRepository.checkNameExists(request.getName(), null)) {
            throw new BusinessException(ErrorCode.RULE_PARAM_NAME_EXISTS,
                    "Rule parameter name already exists: " + request.getName());
        }
        if (ruleParamRepository.checkNameEnExists(request.getNameEn(), null)) {
            throw new BusinessException(ErrorCode.RULE_PARAM_NAME_EXISTS,
                    "Rule parameter English name already exists: " + request.getNameEn());
        }

        RuleParam ruleParam = new RuleParam();
        ruleParam.setName(request.getName());
        ruleParam.setNameEn(request.getNameEn());
        ruleParam.setBusinessObjects(request.getBusinessObjects());
        ruleParam.setStatus(request.getStatus());
        ruleParam.setDataType(request.getDataType());
        ruleParam.setReferenceFieldId(request.getReferenceFieldId());

        return ruleParamRepository.insert(ruleParam);
    }

    @Override
    public void update(Long id, CreateRuleParamRequest request) {
        RuleParam existing = ruleParamRepository.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Rule parameter not found");
        }

        // Check name uniqueness
        if (ruleParamRepository.checkNameExists(request.getName(), id)) {
            throw new BusinessException(ErrorCode.RULE_PARAM_NAME_EXISTS,
                    "Rule parameter name already exists: " + request.getName());
        }
        if (ruleParamRepository.checkNameEnExists(request.getNameEn(), id)) {
            throw new BusinessException(ErrorCode.RULE_PARAM_NAME_EXISTS,
                    "Rule parameter English name already exists: " + request.getNameEn());
        }

        existing.setName(request.getName());
        existing.setNameEn(request.getNameEn());
        existing.setBusinessObjects(request.getBusinessObjects());
        existing.setStatus(request.getStatus());
        existing.setDataType(request.getDataType());
        existing.setReferenceFieldId(request.getReferenceFieldId());

        ruleParamRepository.update(existing);
    }

    @Override
    public void batchUpdateStatus(BatchRuleParamStatusRequest request) {
        ruleParamRepository.batchUpdateStatus(request.getIds(), request.getStatus());
    }

    @Override
    public void delete(Long id) {
        ruleParamRepository.deleteById(id);
    }

    @Override
    public List<RuleParamOptionResponse> getOptions(String status) {
        String queryStatus = (status != null && !status.isEmpty()) ? status : RuleParamStatus.ACTIVE.getCode();
        List<RuleParam> params = ruleParamRepository.findByStatus(queryStatus);

        return params.stream()
                .map(p -> {
                    RuleParamOptionResponse option = new RuleParamOptionResponse();
                    option.setCode(p.getNameEn());
                    option.setName(p.getName());
                    option.setDataType(p.getDataType());
                    return option;
                })
                .collect(Collectors.toList());
    }

    private RuleParamListResponse convertToListResponse(RuleParam entity) {
        RuleParamListResponse response = new RuleParamListResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setNameEn(entity.getNameEn());
        response.setStatus(entity.getStatus());
        response.setStatusName(getStatusName(entity.getStatus()));
        response.setDataType(entity.getDataType());
        response.setDataTypeName(getDataTypeName(entity.getDataType()));
        response.setReferenceFieldId(entity.getReferenceFieldId());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedTime(entity.getCreatedTime());
        response.setUpdatedBy(entity.getUpdatedBy());
        response.setUpdatedTime(entity.getUpdatedTime());

        // Parse business objects JSON
        if (entity.getBusinessObjects() != null) {
            try {
                List<RuleParamListResponse.BusinessObjectDTO> objects = objectMapper.readValue(
                        entity.getBusinessObjects(),
                        new TypeReference<List<RuleParamListResponse.BusinessObjectDTO>>() {}
                );
                response.setBusinessObjects(objects);
            } catch (Exception e) {
                response.setBusinessObjects(new java.util.ArrayList<>());
            }
        }

        return response;
    }

    private RuleParamDetailResponse convertToDetailResponse(RuleParam entity) {
        RuleParamDetailResponse response = new RuleParamDetailResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setNameEn(entity.getNameEn());
        response.setStatus(entity.getStatus());
        response.setDataType(entity.getDataType());
        response.setReferenceFieldId(entity.getReferenceFieldId());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedTime(entity.getCreatedTime());

        // Parse business objects JSON
        if (entity.getBusinessObjects() != null) {
            try {
                List<RuleParamListResponse.BusinessObjectDTO> objects = objectMapper.readValue(
                        entity.getBusinessObjects(),
                        new TypeReference<List<RuleParamListResponse.BusinessObjectDTO>>() {}
                );
                response.setBusinessObjects(objects);
            } catch (Exception e) {
                response.setBusinessObjects(new java.util.ArrayList<>());
            }
        }

        return response;
    }

    private String getStatusName(String statusCode) {
        RuleParamStatus status = RuleParamStatus.fromCode(statusCode);
        return status != null ? status.getName() : statusCode;
    }

    private String getDataTypeName(String dataTypeCode) {
        DataType dataType = DataType.fromCode(dataTypeCode);
        return dataType != null ? dataType.getName() : dataTypeCode;
    }
}