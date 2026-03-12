package com.auth.letter.api;

import com.auth.letter.dto.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/rule-params")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface iRuleParamApi {

    @GET
    Response queryList(
            @QueryParam("name") String name,
            @QueryParam("nameEn") String nameEn,
            @QueryParam("status") String status,
            @QueryParam("pageNum") @DefaultValue("1") Integer pageNum,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize);

    @GET
    @Path("/{id}")
    Response getDetail(@PathParam("id") Long id);

    @POST
    Response create(RuleParamDTO dto);

    @PUT
    @Path("/{id}")
    Response update(@PathParam("id") Long id, RuleParamDTO dto);

    @PUT
    @Path("/batch/activate")
    Response batchActivate(BatchOperationDTO dto);

    @PUT
    @Path("/batch/deactivate")
    Response batchDeactivate(BatchOperationDTO dto);
}