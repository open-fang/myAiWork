package com.auth.letter.controller;

import com.auth.letter.dto.*;
import com.auth.letter.service.RuleParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Rule Parameter Controller
 * REST API endpoints for rule parameter management
 */
@Component
@Path("/api/rule-params")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class RuleParamController {

    private final RuleParamService ruleParamService;

    /**
     * Query rule parameters with pagination
     * GET /api/rule-params
     */
    @GET
    public Response queryList(
            @QueryParam("name") String name,
            @QueryParam("nameEn") String nameEn,
            @QueryParam("status") String status,
            @QueryParam("pageNum") @DefaultValue("1") Integer pageNum,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize) {

        RuleParamQueryDTO queryDTO = new RuleParamQueryDTO();
        queryDTO.setName(name);
        queryDTO.setNameEn(nameEn);
        queryDTO.setStatus(status);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);

        PageResult<RuleParamListVO> result = ruleParamService.queryList(queryDTO);
        return Response.ok(ApiResponse.success(result)).build();
    }

    /**
     * Get rule parameter by id
     * GET /api/rule-params/{id}
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        try {
            RuleParamDTO dto = ruleParamService.getById(id);
            return Response.ok(ApiResponse.success(dto)).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(404, e.getMessage())).build();
        }
    }

    /**
     * Create rule parameter
     * POST /api/rule-params
     */
    @POST
    public Response create(RuleParamDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "名称不能为空")).build();
        }
        if (dto.getNameEn() == null || dto.getNameEn().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "英文名称不能为空")).build();
        }
        if (dto.getDataType() == null || dto.getDataType().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "数据类型不能为空")).build();
        }

        try {
            Long id = ruleParamService.create(dto);
            return Response.ok(ApiResponse.success("创建成功", id)).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    /**
     * Update rule parameter
     * PUT /api/rule-params/{id}
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, RuleParamDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "名称不能为空")).build();
        }
        if (dto.getNameEn() == null || dto.getNameEn().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "英文名称不能为空")).build();
        }

        try {
            ruleParamService.update(id, dto);
            return Response.ok(ApiResponse.success("更新成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    /**
     * Delete rule parameter
     * DELETE /api/rule-params/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            ruleParamService.delete(id);
            return Response.ok(ApiResponse.success("删除成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    /**
     * Batch activate rule parameters
     * PUT /api/rule-params/batch/activate
     */
    @PUT
    @Path("/batch/activate")
    public Response batchActivate(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }

        int count = ruleParamService.batchActivate(dto.getIds());
        String message = count > 0
                ? String.format("成功生效%d条数据", count)
                : "没有符合条件的数据可生效";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    /**
     * Batch deactivate rule parameters
     * PUT /api/rule-params/batch/deactivate
     */
    @PUT
    @Path("/batch/deactivate")
    public Response batchDeactivate(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }

        int count = ruleParamService.batchDeactivate(dto.getIds());
        String message = count > 0
                ? String.format("成功失效%d条数据", count)
                : "没有符合条件的数据可失效";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    /**
     * Get all active rule parameters for dropdown
     * GET /api/rule-params/active
     */
    @GET
    @Path("/active")
    public Response getAllActive() {
        List<RuleParamListVO> result = ruleParamService.getAllActive();
        return Response.ok(ApiResponse.success(result)).build();
    }
}