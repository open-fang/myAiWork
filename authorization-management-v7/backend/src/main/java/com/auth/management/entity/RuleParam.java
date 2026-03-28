package com.auth.management.entity;

import com.auth.management.typehandler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Rule Parameter Entity
 */
@Data
@TableName(value = "rule_params", autoResultMap = true)
public class RuleParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String nameEn;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String businessObjects;

    private String status;

    private String dataType;

    private Long referenceFieldId;

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