package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Rule Group DTO
 */
@Data
public class RuleGroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ruleName;

    @NotNull(message = "Logic type is required")
    private String logicType;

    @NotNull(message = "Conditions are required")
    private List<RuleConditionDTO> conditions;
}