package com.auth.letter.api;

import com.auth.letter.dto.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/auth-letters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface iAuthLetterApi {

    @GET
    Response queryList(
            @QueryParam("name") String name,
            @QueryParam("authTargetLevel") List<String> authTargetLevel,
            @QueryParam("applicableRegion") List<String> applicableRegion,
            @QueryParam("authPublishLevel") List<String> authPublishLevel,
            @QueryParam("authPublishOrg") List<String> authPublishOrg,
            @QueryParam("publishYear") Integer publishYear,
            @QueryParam("status") String status,
            @QueryParam("pageNum") @DefaultValue("1") Integer pageNum,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize);

    @GET
    @Path("/{id}")
    Response getDetail(@PathParam("id") Long id);

    @POST
    Response create(AuthLetterDetailDTO dto);

    @PUT
    @Path("/{id}")
    Response update(@PathParam("id") Long id, AuthLetterDetailDTO dto);

    @DELETE
    @Path("/{id}")
    Response delete(@PathParam("id") Long id);

    @PUT
    @Path("/{id}/publish")
    Response publish(@PathParam("id") Long id);

    @PUT
    @Path("/{id}/expire")
    Response expire(@PathParam("id") Long id);

    @PUT
    @Path("/batch/publish")
    Response batchPublish(BatchOperationDTO dto);

    @PUT
    @Path("/batch/expire")
    Response batchExpire(BatchOperationDTO dto);

    @DELETE
    @Path("/batch")
    Response batchDelete(BatchOperationDTO dto);
}