package com.auth.letter.controller;

import com.auth.letter.dto.*;
import com.auth.letter.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Lookup Controller
 * REST API endpoints for dropdown options
 */
@Component
@Path("/api/lookup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    /**
     * Get lookup options by type
     * GET /api/lookup/{type}
     */
    @GET
    @Path("/{type}")
    public Response getLookupOptions(@PathParam("type") String type) {
        List<Map<String, String>> options = lookupService.getLookupOptions(type);
        return Response.ok(ApiResponse.success(options)).build();
    }

    /**
     * Get organization tree
     * GET /api/lookup/org/tree
     */
    @GET
    @Path("/org/tree")
    public Response getOrgTree() {
        List<TreeNodeVO> tree = lookupService.getOrgTree();
        return Response.ok(ApiResponse.success(tree)).build();
    }

    /**
     * Get applicable region tree
     * GET /api/lookup/region/tree
     */
    @GET
    @Path("/region/tree")
    public Response getApplicableRegionTree() {
        List<TreeNodeVO> tree = lookupService.getApplicableRegionTree();
        return Response.ok(ApiResponse.success(tree)).build();
    }

    /**
     * Get industry tree
     * GET /api/lookup/industry/tree
     */
    @GET
    @Path("/industry/tree")
    public Response getIndustryTree() {
        List<TreeNodeVO> tree = lookupService.getIndustryTree();
        return Response.ok(ApiResponse.success(tree)).build();
    }
}