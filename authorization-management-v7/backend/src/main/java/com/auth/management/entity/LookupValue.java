package com.auth.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Lookup Value Entity
 */
@Data
@TableName("lookup_values")
public class LookupValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long typeId;

    private String valueCode;

    private String valueName;

    private String parentCode;

    private Integer level;

    private Integer sortOrder;

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