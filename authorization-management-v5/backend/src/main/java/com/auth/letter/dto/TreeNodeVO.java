package com.auth.letter.dto;

import lombok.Data;
import java.util.List;

/**
 * Tree Node VO for organization/region tree
 */
@Data
public class TreeNodeVO {
    private String code;
    private String name;
    private String parentCode;
    private List<TreeNodeVO> children;
}