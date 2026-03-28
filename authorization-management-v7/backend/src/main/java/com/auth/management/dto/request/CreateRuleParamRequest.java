package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Create Rule Parameter Request
 */
@Data
public class CreateRuleParamRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "English name is required")
    @Size(max = 100, message = "English name must not exceed 100 characters")
    private String nameEn;

    @NotBlank(message = "Business objects are required")
    private String businessObjects;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Data type is required")
    private String dataType;

    private Long referenceFieldId;
}