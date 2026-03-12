package com.auth.letter.api.impl;

import com.auth.letter.api.iAttachmentApi;
import com.auth.letter.dto.*;
import com.auth.letter.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

@Component
@RequiredArgsConstructor
public class AttachmentApiImpl implements iAttachmentApi {

    private final AttachmentService attachmentService;

    @Override
    public Response queryList(Long authLetterId, Integer pageNum, Integer pageSize) {
        if (authLetterId == null) {
            return Response.ok(ApiResponse.error(400, "授权书ID不能为空")).build();
        }
        PageResult<AttachmentVO> result = attachmentService.queryList(authLetterId, pageNum, pageSize);
        return Response.ok(ApiResponse.success(result)).build();
    }

    @Override
    public Response create(AttachmentVO dto) {
        if (dto.getDocName() == null || dto.getDocName().trim().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "文档名称不能为空")).build();
        }
        Long id = attachmentService.create(0L, dto); // authLetterId will be set separately
        return Response.ok(ApiResponse.success("上传成功", id)).build();
    }

    @Override
    public Response delete(Long id) {
        attachmentService.delete(id);
        return Response.ok(ApiResponse.success("删除成功")).build();
    }
}