package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

@Data
public class AuthLetterDetailDTO {
    private Long id;
    private String name;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private List<String> authTargetLevel;
    private List<String> applicableRegion;
    private Integer publishYear;
    private String contentSummary;
    private List<SceneDTO> scenes;
}