package com.auth.letter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Rule
 */
@Data
public class RuleDTO {

    private Long id;

    @NotBlank(message = "规则名称不能为空")
    private String name;

    private String description;

    private Integer orderIndex;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long sceneId;

    private List<ConditionDTO> conditions;
}