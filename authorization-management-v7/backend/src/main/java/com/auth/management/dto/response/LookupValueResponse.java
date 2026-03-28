package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Lookup Value Response (for dropdown)
 */
@Data
public class LookupValueResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private List<LookupValueResponse> children;
}