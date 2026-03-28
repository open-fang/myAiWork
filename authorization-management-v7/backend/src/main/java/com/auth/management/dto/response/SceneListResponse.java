package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scene List Response
 */
@Data
public class SceneListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sceneName;
    private List<String> industry;
    private String industryName;
    private String businessScene;
    private String businessSceneName;
    private String decisionLevel;
    private String decisionLevelName;
    private String specificRule;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}