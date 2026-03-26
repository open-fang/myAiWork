package com.auth.letter.controller;

import com.auth.letter.common.PageResult;
import com.auth.letter.common.Result;
import com.auth.letter.dto.request.AuthLetterQueryRequest;
import com.auth.letter.dto.request.AuthLetterRequest;
import com.auth.letter.dto.request.BatchRequest;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.service.AuthLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;

/**
 * 授权书控制器
 */
@RestController
@RequestMapping("/auth-letters")
public class AuthLetterController {

    @Autowired
    private AuthLetterService authLetterService;

    /**
     * 查询授权书列表
     */
    @GetMapping
    public Result<PageResult<AuthLetter>> list(AuthLetterQueryRequest request) {
        PageResult<AuthLetter> result = authLetterService.list(request);
        return Result.success(result);
    }

    /**
     * 获取授权书详情
     */
    @GetMapping("/{id}")
    public Result<AuthLetter> getById(@PathVariable Long id) {
        AuthLetter authLetter = authLetterService.getById(id);
        return Result.success(authLetter);
    }

    /**
     * 创建授权书
     */
    @PostMapping
    public Result<Long> create(@RequestBody AuthLetterRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        Long id = authLetterService.create(request, operator);
        return Result.success(id);
    }

    /**
     * 更新授权书
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AuthLetterRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        authLetterService.update(id, request, operator);
        return Result.success();
    }

    /**
     * 删除授权书
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        authLetterService.delete(id, operator);
        return Result.success();
    }

    /**
     * 发布授权书
     */
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        authLetterService.publish(id, operator);
        return Result.success();
    }

    /**
     * 失效授权书
     */
    @PostMapping("/{id}/invalidate")
    public Result<Void> invalidate(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        authLetterService.invalidate(id, operator);
        return Result.success();
    }

    /**
     * 批量操作
     */
    @PostMapping("/batch")
    public Result<Void> batch(@RequestBody BatchRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        authLetterService.batchOperation(request, operator);
        return Result.success();
    }
}