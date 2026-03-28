package com.auth.management.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Create Response (returns ID)
 */
@Data
public class CreateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    public CreateResponse(Long id) {
        this.id = id;
    }
}