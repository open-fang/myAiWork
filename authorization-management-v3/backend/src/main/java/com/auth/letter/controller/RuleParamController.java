package com.auth.letter.controller;

import com.auth.letter.dto.*;
import com.auth.letter.service.RuleParamService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 规则参数控制器
 * Uses JAX-RS annotations (@Path) for URL mapping
 */
@Component
@Path("/api/rule-params")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class RuleParamController {

    private final RuleParamService ruleParamService;

    /**
     * 分页查询规则参数列表
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
     * 获取规则参数详情
     */
    @GET
    @Path("/{id}")
    public Response getDetail(@PathParam("id") Long id) {
        RuleParamListVO detail = ruleParamService.getDetail(id);
        if (detail == null) {
            return Response.ok(ApiResponse.error(404, "规则参数不存在")).build();
        }
        return Response.ok(ApiResponse.success(detail)).build();
    }

    /**
     * 创建规则参数
     */
    @POST
    public Response create(RuleParamDTO dto) {
        // 参数校验
        if (dto.getName() == null || dto.getName().isBlank()) {
            return Response.ok(ApiResponse.error(400, "名称不能为空")).build();
        }
        if (dto.getNameEn() == null || dto.getNameEn().isBlank()) {
            return Response.ok(ApiResponse.error(400, "名称英文不能为空")).build();
        }
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            return Response.ok(ApiResponse.error(400, "状态不能为空")).build();
        }
        if (dto.getDataType() == null || dto.getDataType().isBlank()) {
            return Response.ok(ApiResponse.error(400, "数据类型不能为空")).build();
        }

        Long id = ruleParamService.create(dto);
        return Response.ok(ApiResponse.success("创建成功", id)).build();
    }

    /**
     * 更新规则参数
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, RuleParamDTO dto) {
        // 参数校验
        if (dto.getName() == null || dto.getName().isBlank()) {
            return Response.ok(ApiResponse.error(400, "名称不能为空")).build();
        }
        if (dto.getNameEn() == null || dto.getNameEn().isBlank()) {
            return Response.ok(ApiResponse.error(400, "名称英文不能为空")).build();
        }

        try {
            ruleParamService.update(id, dto);
            return Response.ok(ApiResponse.success("更新成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(404, e.getMessage())).build();
        }
    }

    /**
     * 批量激活
     */
    @PUT
    @Path("/batch/activate")
    public Response batchActivate(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }

        int count = ruleParamService.batchActivate(dto.getIds());
        String message = count > 0
                ? String.format("成功激活%d条数据", count)
                : "没有符合条件的数据可激活";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    /**
     * 批量失效
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
}