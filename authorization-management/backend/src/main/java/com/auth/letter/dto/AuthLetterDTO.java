package com.auth.letter.dto;

import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Authorization Letter
 */
@Data
public class AuthLetterDTO {

    private Long id;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String description;

    private AuthLetterStatus status;

    private OrgLevel publishLevel;

    private OrgLevel authorizedLevel;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    private List<SceneDTO> scenes;
}