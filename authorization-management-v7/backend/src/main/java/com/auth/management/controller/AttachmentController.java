package com.auth.management.controller;

import com.auth.management.dto.request.BatchDeleteRequest;
import com.auth.management.dto.request.CreateAttachmentRequest;
import com.auth.management.dto.response.ApiResponse;
import com.auth.management.dto.response.AttachmentResponse;
import com.auth.management.dto.response.CreateResponse;
import com.auth.management.dto.response.PageResponse;
import com.auth.management.service.AttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Attachment Controller
 */
@RestController
@RequestMapping("/api/v1/auth-letters/{authLetterId}/attachments")
@Api(tags = "Attachment Management")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping
    @ApiOperation("Query attachment list")
    public ApiResponse<PageResponse<AttachmentResponse>> queryList(
            @PathVariable Long authLetterId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResponse<AttachmentResponse> result = attachmentService.queryList(authLetterId, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @PostMapping
    @ApiOperation("Upload attachment")
    public ApiResponse<CreateResponse> upload(
            @PathVariable Long authLetterId,
            @Valid @RequestBody CreateAttachmentRequest request) {
        Long id = attachmentService.upload(authLetterId, request);
        return ApiResponse.success(new CreateResponse(id));
    }

    @GetMapping("/{id}/download")
    @ApiOperation("Download attachment")
    public void download(
            @PathVariable Long authLetterId,
            @PathVariable Long id,
            HttpServletResponse response) {
        attachmentService.download(authLetterId, id, response);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete attachment")
    public ApiResponse<Void> delete(
            @PathVariable Long authLetterId,
            @PathVariable Long id) {
        attachmentService.delete(authLetterId, id);
        return ApiResponse.success();
    }

    @PostMapping("/batch-delete")
    @ApiOperation("Batch delete attachments")
    public ApiResponse<Void> batchDelete(
            @PathVariable Long authLetterId,
            @Valid @RequestBody BatchDeleteRequest request) {
        attachmentService.batchDelete(authLetterId, request.getIds());
        return ApiResponse.success();
    }
}