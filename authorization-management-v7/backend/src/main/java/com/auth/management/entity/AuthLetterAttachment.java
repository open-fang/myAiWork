package com.auth.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Authorization Letter Attachment Entity
 */
@Data
@TableName("auth_letter_attachments")
public class AuthLetterAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long authLetterId;

    private String docId;

    private String docName;

    private String docType;

    private Integer isEncrypted;

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