package com.auth.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Authorization Letter Operation Log Entity
 */
@Data
@TableName("auth_letter_logs")
public class AuthLetterLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long authLetterId;

    private String operation;

    private String operator;

    private String operatorName;

    private LocalDateTime operationTime;
}