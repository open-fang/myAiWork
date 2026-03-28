package com.auth.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Create Attachment Request
 */
@Data
public class CreateAttachmentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Document ID is required")
    private String docId;

    @NotBlank(message = "Document name is required")
    private String docName;

    @NotBlank(message = "Document type is required")
    private String docType;
}