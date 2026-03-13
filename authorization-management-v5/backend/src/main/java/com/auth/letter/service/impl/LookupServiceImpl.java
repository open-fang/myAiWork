package com.auth.letter.service.impl;

import com.auth.letter.dto.TreeNodeVO;
import com.auth.letter.entity.AuthLookup;
import com.auth.letter.repository.AuthLookupRepository;
import com.auth.letter.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lookup Service Implementation
 */
@Service
@RequiredArgsConstructor
public class LookupServiceImpl implements LookupService {

    private final AuthLookupRepository lookupRepository;

    @Override
    public List<Map<String, String>> getLookupOptions(String lookupType) {
        List<AuthLookup> lookups = lookupRepository.findByLookupTypeAndStatusOrderBySortOrderAsc(lookupType, "ACTIVE");
        return lookups.stream()
                .map(l -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", l.getLookupCode());
                    map.put("name", l.getLookupName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TreeNodeVO> getOrgTree() {
        return buildTree("ORG_TREE");
    }

    @Override
    public List<TreeNodeVO> getApplicableRegionTree() {
        return buildTree("APPLICABLE_REGION");
    }

    @Override
    public List<TreeNodeVO> getIndustryTree() {
        return buildTree("INDUSTRY");
    }

    private List<TreeNodeVO> buildTree(String lookupType) {
        List<AuthLookup> allLookups = lookupRepository.findByLookupTypeAndStatusOrderBySortOrderAsc(lookupType, "ACTIVE");

        Map<String, TreeNodeVO> nodeMap = new HashMap<>();
        List<TreeNodeVO> rootNodes = new ArrayList<>();

        // Create all nodes first
        for (AuthLookup lookup : allLookups) {
            TreeNodeVO node = new TreeNodeVO();
            node.setCode(lookup.getLookupCode());
            node.setName(lookup.getLookupName());
            node.setParentCode(lookup.getParentCode());
            node.setChildren(new ArrayList<>());
            nodeMap.put(lookup.getLookupCode(), node);
        }

        // Build tree structure
        for (AuthLookup lookup : allLookups) {
            TreeNodeVO node = nodeMap.get(lookup.getLookupCode());
            if (lookup.getParentCode() == null || lookup.getParentCode().isEmpty()) {
                rootNodes.add(node);
            } else {
                TreeNodeVO parent = nodeMap.get(lookup.getParentCode());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return rootNodes;
    }
}