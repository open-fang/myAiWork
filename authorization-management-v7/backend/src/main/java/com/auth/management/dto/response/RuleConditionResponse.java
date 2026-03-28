package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Rule Condition Response
 */
@Data
public class RuleConditionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fieldCode;
    private String fieldName;
    private String operator;
    private String operatorSymbol;
    private String compareType;
    private String compareTypeDesc;
    private String compareFieldCode;
    private String compareFieldName;
    private String compareValue;
    private String compareUnit;
    private String compareUnitName;
    private String logicType;
    private Boolean isGroup;
    private String groupLogicType;
    private Integer sortOrder;
    private List<RuleConditionResponse> children;
}