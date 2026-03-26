package com.auth.letter.controller;

import com.auth.letter.common.PageResult;
import com.auth.letter.common.Result;
import com.auth.letter.dto.request.SceneRequest;
import com.auth.letter.entity.Scene;
import com.auth.letter.service.SceneService;
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
 * 场景控制器
 */
@RestController
@RequestMapping("/auth-letters/{authLetterId}/scenes")
public class SceneController {

    @Autowired
    private SceneService sceneService;

    /**
     * 查询场景列表
     */
    @GetMapping
    public Result<PageResult<Scene>> list(
            @PathVariable Long authLetterId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Scene> result = sceneService.list(authLetterId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取场景详情
     */
    @GetMapping("/{id}")
    public Result<Scene> getById(@PathVariable Long id) {
        Scene scene = sceneService.getById(id);
        return Result.success(scene);
    }

    /**
     * 创建场景
     */
    @PostMapping
    public Result<Long> create(@PathVariable Long authLetterId, @RequestBody SceneRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        Long id = sceneService.create(authLetterId, request, operator);
        return Result.success(id);
    }

    /**
     * 更新场景
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SceneRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        sceneService.update(id, request, operator);
        return Result.success();
    }

    /**
     * 删除场景
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        sceneService.delete(id, operator);
        return Result.success();
    }

    /**
     * 批量删除场景
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        sceneService.batchDelete(ids, operator);
        return Result.success();
    }
}