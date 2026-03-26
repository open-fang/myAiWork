package com.auth.letter.controller;

import com.auth.letter.common.PageResult;
import com.auth.letter.common.Result;
import com.auth.letter.entity.Attachment;
import com.auth.letter.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 附件控制器
 */
@RestController
@RequestMapping("/auth-letters/{authLetterId}/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 查询附件列表
     */
    @GetMapping
    public Result<PageResult<Attachment>> list(
            @PathVariable Long authLetterId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Attachment> result = attachmentService.list(authLetterId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取附件详情
     */
    @GetMapping("/{id}")
    public Result<Attachment> getById(@PathVariable Long id) {
        Attachment attachment = attachmentService.getById(id);
        return Result.success(attachment);
    }

    /**
     * 上传附件
     */
    @PostMapping
    public Result<Long> upload(@PathVariable Long authLetterId, @RequestBody AttachmentRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        Long id = attachmentService.create(authLetterId, request.getDocId(), request.getDocName(), request.getDocType(), operator);
        return Result.success(id);
    }

    /**
     * 删除附件
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        attachmentService.delete(id, operator);
        return Result.success();
    }

    /**
     * 批量删除附件
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        attachmentService.batchDelete(ids, operator);
        return Result.success();
    }

    /**
     * 附件请求
     */
    public static class AttachmentRequest {
        private String docId;
        private String docName;
        private String docType;

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocName() {
            return docName;
        }

        public void setDocName(String docName) {
            this.docName = docName;
        }

        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
        }
    }
}