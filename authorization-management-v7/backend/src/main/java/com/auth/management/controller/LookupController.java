package com.auth.management.controller;

import com.auth.management.dto.response.ApiResponse;
import com.auth.management.dto.response.LookupValueResponse;
import com.auth.management.service.LookupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lookup Controller
 */
@RestController
@RequestMapping("/api/v1/lookup-values")
@Api(tags = "Lookup Management")
public class LookupController {

    @Autowired
    private LookupService lookupService;

    @GetMapping("/{typeCode}")
    @ApiOperation("Get lookup values by type code")
    public ApiResponse<List<LookupValueResponse>> getLookupValues(@PathVariable String typeCode) {
        List<LookupValueResponse> values = lookupService.getLookupValues(typeCode);
        return ApiResponse.success(values);
    }
}