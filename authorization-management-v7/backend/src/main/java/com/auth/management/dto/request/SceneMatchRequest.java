package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * Scene Match Request
 */
@Data
public class SceneMatchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Authorization letter ID is required")
    private Long authLetterId;

    @NotNull(message = "Data is required")
    private Map<String, Object> data;
}