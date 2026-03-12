package com.auth.letter.api;

import com.auth.letter.dto.ApiResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/lookup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface iLookupApi {

    @GET
    @Path("/{code}")
    Response getLookupValues(@PathParam("code") String code);

    @GET
    @Path("/org/tree")
    Response getOrgTree();
}