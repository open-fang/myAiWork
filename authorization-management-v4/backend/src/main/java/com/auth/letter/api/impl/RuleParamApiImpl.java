package com.auth.letter.api.impl;

import com.auth.letter.api.iRuleParamApi;
import com.auth.letter.dto.*;
import com.auth.letter.service.RuleParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/api/rule-params")
@RequiredArgsConstructor
public class RuleParamApiImpl implements iRuleParamApi {

    private final RuleParamService ruleParamService;

    @Override
    public Response queryList(String name, String nameEn, String status, Integer pageNum, Integer pageSize) {
        RuleParamQueryDTO queryDTO = new RuleParamQueryDTO();
        queryDTO.setName(name);
        queryDTO.setNameEn(nameEn);
        queryDTO.setStatus(status);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);

        PageResult<RuleParamListVO> result = ruleParamService.queryList(queryDTO);
        return Response.ok(ApiResponse.success(result)).build();
    }

    @Override
    public Response getDetail(Long id) {
        try {
            RuleParamDTO detail = ruleParamService.getDetail(id);
            return Response.ok(ApiResponse.success(detail)).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(404, e.getMessage())).build();
        }
    }

    @Override
    public Response create(RuleParamDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "名称不能为空")).build();
        }
        if (dto.getNameEn() == null || dto.getNameEn().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "名称英文不能为空")).build();
        }
        if (dto.getDataType() == null || dto.getDataType().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "数据类型不能为空")).build();
        }
        Long id = ruleParamService.create(dto);
        return Response.ok(ApiResponse.success("创建成功", id)).build();
    }

    @Override
    public Response update(Long id, RuleParamDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "名称不能为空")).build();
        }
        if (dto.getNameEn() == null || dto.getNameEn().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "名称英文不能为空")).build();
        }
        if (dto.getDataType() == null || dto.getDataType().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "数据类型不能为空")).build();
        }
        try {
            ruleParamService.update(id, dto);
            return Response.ok(ApiResponse.success("更新成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    @Override
    public Response batchActivate(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }
        int count = ruleParamService.batchActivate(dto.getIds());
        String message = count > 0 ? String.format("成功生效%d条数据", count) : "没有符合条件的数据可生效";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    @Override
    public Response batchDeactivate(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }
        int count = ruleParamService.batchDeactivate(dto.getIds());
        String message = count > 0 ? String.format("成功失效%d条数据", count) : "没有符合条件的数据可失效";
        return Response.ok(ApiResponse.success(message, count)).build();
    }
}