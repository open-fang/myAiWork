package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Rule Condition DTO
 */
@Data
public class RuleConditionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fieldCode;

    private String operator;

    private String compareType;

    private String compareFieldCode;

    private String compareValue;

    private String compareUnit;

    @NotNull(message = "Logic type is required")
    private String logicType;

    @NotNull(message = "Is group flag is required")
    private Boolean isGroup;

    private String groupLogicType;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;

    private List<RuleConditionDTO> children;
}