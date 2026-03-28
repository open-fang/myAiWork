package com.auth.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Authorization Letter Rule Condition Entity
 */
@Data
@TableName("auth_letter_rule_conditions")
public class AuthLetterRuleCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ruleId;

    private Long parentConditionId;

    private String fieldCode;

    private String operator;

    private String compareType;

    private String compareFieldCode;

    private String compareValue;

    private String compareUnit;

    private String logicType;

    private Integer isGroup;

    private String groupLogicType;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updatedBy;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleteFlag;
}