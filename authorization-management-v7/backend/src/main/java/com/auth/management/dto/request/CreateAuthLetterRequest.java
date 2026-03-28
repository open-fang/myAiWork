package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Create Authorization Letter Request
 */
@Data
public class CreateAuthLetterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Authorization letter name is required")
    @Size(max = 200, message = "Authorization letter name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Authorization object level is required")
    private String authObjectLevel;

    @NotBlank(message = "Applicable region is required")
    private String applicableRegion;

    @NotBlank(message = "Authorization publish level is required")
    private String authPublishLevel;

    @NotBlank(message = "Authorization publish organization is required")
    private String authPublishOrg;

    private Integer publishYear;

    @NotBlank(message = "Summary is required")
    @Size(max = 4000, message = "Summary must not exceed 4000 characters")
    private String summary;
}