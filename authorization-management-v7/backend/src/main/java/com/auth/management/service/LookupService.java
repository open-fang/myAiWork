package com.auth.management.service;

import com.auth.management.dto.response.LookupValueResponse;

import java.util.List;

/**
 * Lookup Service
 */
public interface LookupService {

    /**
     * Get lookup values by type code
     * @param typeCode the type code
     * @return list of lookup values (flat or tree structure based on type)
     */
    List<LookupValueResponse> getLookupValues(String typeCode);
}