package com.auth.management.entity;

import com.auth.management.typehandler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Authorization Letter Entity
 */
@Data
@TableName(value = "auth_letters", autoResultMap = true)
public class AuthLetter implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String authObjectLevel;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String applicableRegion;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String authPublishLevel;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String authPublishOrg;

    private Integer publishYear;

    private String summary;

    private String status;

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