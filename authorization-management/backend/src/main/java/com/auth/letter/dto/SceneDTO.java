package com.auth.letter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Scene
 */
@Data
public class SceneDTO {

    private Long id;

    @NotBlank(message = "场景名称不能为空")
    private String name;

    private String description;

    private Integer decisionLevel;

    private Integer orderIndex;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long authLetterId;

    private List<RuleDTO> rules;
}