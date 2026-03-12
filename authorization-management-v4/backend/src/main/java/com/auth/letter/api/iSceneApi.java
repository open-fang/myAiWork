package com.auth.letter.api;

import com.auth.letter.dto.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/scenes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface iSceneApi {

    @GET
    Response queryList(
            @QueryParam("authLetterId") Long authLetterId,
            @QueryParam("pageNum") @DefaultValue("1") Integer pageNum,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize);

    @POST
    Response create(SceneDTO dto);

    @PUT
    @Path("/{id}")
    Response update(@PathParam("id") Long id, SceneDTO dto);

    @DELETE
    @Path("/{id}")
    Response delete(@PathParam("id") Long id);

    @DELETE
    @Path("/batch")
    Response batchDelete(BatchOperationDTO dto);
}