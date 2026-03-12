package com.auth.letter.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuthOperationLog {
    private Long id;
    private Long authLetterId;
    private String operation;
    private String operator;
    private LocalDateTime operatedAt;
}