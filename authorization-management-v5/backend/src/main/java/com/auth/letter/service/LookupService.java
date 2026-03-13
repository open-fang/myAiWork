package com.auth.letter.service;

import com.auth.letter.dto.TreeNodeVO;

import java.util.List;
import java.util.Map;

/**
 * Lookup Service Interface
 */
public interface LookupService {

    /**
     * Get lookup options by type
     */
    List<Map<String, String>> getLookupOptions(String lookupType);

    /**
     * Get organization tree
     */
    List<TreeNodeVO> getOrgTree();

    /**
     * Get applicable region tree
     */
    List<TreeNodeVO> getApplicableRegionTree();

    /**
     * Get industry tree
     */
    List<TreeNodeVO> getIndustryTree();
}