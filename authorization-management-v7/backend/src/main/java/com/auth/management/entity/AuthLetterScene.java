package com.auth.management.entity;

import com.auth.management.typehandler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Authorization Letter Scene Entity
 */
@Data
@TableName(value = "auth_letter_scenes", autoResultMap = true)
public class AuthLetterScene implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long authLetterId;

    private String sceneName;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String industry;

    private String businessScene;

    private String decisionLevel;

    private String specificRule;

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