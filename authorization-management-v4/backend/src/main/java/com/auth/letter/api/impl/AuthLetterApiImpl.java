package com.auth.letter.api.impl;

import com.auth.letter.api.iAuthLetterApi;
import com.auth.letter.dto.*;
import com.auth.letter.service.AuthLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/api/auth-letters")
@RequiredArgsConstructor
public class AuthLetterApiImpl implements iAuthLetterApi {

    private final AuthLetterService authLetterService;

    @Override
    public Response queryList(String name, List<String> authTargetLevel, List<String> applicableRegion,
                               List<String> authPublishLevel, List<String> authPublishOrg,
                               Integer publishYear, String status, Integer pageNum, Integer pageSize) {
        AuthLetterQueryDTO queryDTO = new AuthLetterQueryDTO();
        queryDTO.setName(name);
        queryDTO.setAuthTargetLevel(authTargetLevel);
        queryDTO.setApplicableRegion(applicableRegion);
        queryDTO.setAuthPublishLevel(authPublishLevel);
        queryDTO.setAuthPublishOrg(authPublishOrg);
        queryDTO.setPublishYear(publishYear);
        queryDTO.setStatus(status);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);

        PageResult<AuthLetterListVO> result = authLetterService.queryList(queryDTO);
        return Response.ok(ApiResponse.success(result)).build();
    }

    @Override
    public Response getDetail(Long id) {
        try {
            AuthLetterDetailVO detail = authLetterService.getDetail(id);
            return Response.ok(ApiResponse.success(detail)).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(404, e.getMessage())).build();
        }
    }

    @Override
    public Response create(AuthLetterDetailDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "授权书名称不能为空")).build();
        }
        Long id = authLetterService.create(dto);
        return Response.ok(ApiResponse.success("创建成功", id)).build();
    }

    @Override
    public Response update(Long id, AuthLetterDetailDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "授权书名称不能为空")).build();
        }
        try {
            authLetterService.update(id, dto);
            return Response.ok(ApiResponse.success("更新成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    @Override
    public Response delete(Long id) {
        try {
            authLetterService.delete(id);
            return Response.ok(ApiResponse.success("删除成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    @Override
    public Response publish(Long id) {
        try {
            authLetterService.publish(id);
            return Response.ok(ApiResponse.success("发布成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    @Override
    public Response expire(Long id) {
        try {
            authLetterService.expire(id);
            return Response.ok(ApiResponse.success("失效成功")).build();
        } catch (RuntimeException e) {
            return Response.ok(ApiResponse.error(400, e.getMessage())).build();
        }
    }

    @Override
    public Response batchPublish(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }
        int count = authLetterService.batchPublish(dto.getIds());
        String message = count > 0 ? String.format("成功发布%d条数据", count) : "没有符合条件的数据可发布";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    @Override
    public Response batchExpire(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }
        int count = authLetterService.batchExpire(dto.getIds());
        String message = count > 0 ? String.format("成功失效%d条数据", count) : "没有符合条件的数据可失效";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    @Override
    public Response batchDelete(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }
        int count = authLetterService.batchDelete(dto.getIds());
        String message = count > 0 ? String.format("成功删除%d条数据", count) : "没有符合条件的数据可删除";
        return Response.ok(ApiResponse.success(message, count)).build();
    }
}