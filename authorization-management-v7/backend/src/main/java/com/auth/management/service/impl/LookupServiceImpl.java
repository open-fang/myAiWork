package com.auth.management.service.impl;

import com.auth.management.dto.response.LookupValueResponse;
import com.auth.management.entity.LookupType;
import com.auth.management.entity.LookupValue;
import com.auth.management.repository.LookupRepository;
import com.auth.management.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lookup Service Implementation
 */
@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    private LookupRepository lookupRepository;

    // Tree-structured type codes
    private static final List<String> TREE_TYPE_CODES = List.of(
            "APPLICABLE_REGION",
            "AUTH_PUBLISH_ORG",
            "INDUSTRY"
    );

    @Override
    public List<LookupValueResponse> getLookupValues(String typeCode) {
        LookupType lookupType = lookupRepository.findByTypeCode(typeCode);
        if (lookupType == null) {
            return new ArrayList<>();
        }

        if (TREE_TYPE_CODES.contains(typeCode)) {
            // Return tree structure
            List<LookupValue> allValues = lookupRepository.findTreeByTypeId(lookupType.getId());
            return buildTree(allValues, null);
        } else {
            // Return flat list
            List<LookupValue> values = lookupRepository.findByTypeId(lookupType.getId());
            return values.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    private List<LookupValueResponse> buildTree(List<LookupValue> allValues, String parentCode) {
        List<LookupValueResponse> result = new ArrayList<>();

        // Filter values by parent code
        List<LookupValue> children = allValues.stream()
                .filter(v -> (parentCode == null && v.getParentCode() == null) ||
                        (parentCode != null && parentCode.equals(v.getParentCode())))
                .collect(Collectors.toList());

        for (LookupValue value : children) {
            LookupValueResponse response = convertToResponse(value);
            // Recursively build children
            List<LookupValueResponse> childResponses = buildTree(allValues, value.getValueCode());
            if (!childResponses.isEmpty()) {
                response.setChildren(childResponses);
            }
            result.add(response);
        }

        return result;
    }

    private LookupValueResponse convertToResponse(LookupValue value) {
        LookupValueResponse response = new LookupValueResponse();
        response.setCode(value.getValueCode());
        response.setName(value.getValueName());
        return response;
    }
}