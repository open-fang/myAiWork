package com.auth.letter.controller;

import com.auth.letter.common.PageResult;
import com.auth.letter.common.Result;
import com.auth.letter.dto.request.RuleParamRequest;
import com.auth.letter.entity.RuleParam;
import com.auth.letter.service.RuleParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 规则参数控制器
 */
@RestController
@RequestMapping("/rule-params")
public class RuleParamController {

    @Autowired
    private RuleParamService ruleParamService;

    /**
     * 查询规则参数列表
     */
    @GetMapping
    public Result<PageResult<RuleParam>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String nameEn,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<RuleParam> result = ruleParamService.list(name, nameEn, status, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取规则参数详情
     */
    @GetMapping("/{id}")
    public Result<RuleParam> getById(@PathVariable Long id) {
        RuleParam ruleParam = ruleParamService.getById(id);
        return Result.success(ruleParam);
    }

    /**
     * 获取所有生效的规则参数
     */
    @GetMapping("/active")
    public Result<List<RuleParam>> getAllActive() {
        List<RuleParam> list = ruleParamService.getAllActive();
        return Result.success(list);
    }

    /**
     * 创建规则参数
     */
    @PostMapping
    public Result<Long> create(@RequestBody RuleParamRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        Long id = ruleParamService.create(request, operator);
        return Result.success(id);
    }

    /**
     * 更新规则参数
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RuleParamRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        ruleParamService.update(id, request, operator);
        return Result.success();
    }

    /**
     * 删除规则参数
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        ruleParamService.delete(id, operator);
        return Result.success();
    }

    /**
     * 批量更新状态
     */
    @PostMapping("/batch-status")
    public Result<Void> batchUpdateStatus(@RequestBody BatchStatusRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        ruleParamService.batchUpdateStatus(request.getIds(), request.getStatus(), operator);
        return Result.success();
    }

    /**
     * 批量状态请求
     */
    public static class BatchStatusRequest {
        private List<Long> ids;
        private String status;

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}