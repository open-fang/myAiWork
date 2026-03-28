package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Rule Group Response
 */
@Data
public class RuleGroupResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruleName;
    private String logicType;
    private List<RuleConditionResponse> conditions;
}