package com.auth.letter.controller;

import com.auth.letter.common.PageResult;
import com.auth.letter.common.Result;
import com.auth.letter.dto.request.AnswerRequest;
import com.auth.letter.dto.request.QuestionQueryRequest;
import com.auth.letter.dto.request.QuestionRequest;
import com.auth.letter.dto.response.AnswerResponse;
import com.auth.letter.dto.response.QuestionResponse;
import com.auth.letter.service.QuestionService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问卷题目控制器
 */
@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 查询题目列表
     */
    @GetMapping
    public Result<PageResult<QuestionResponse>> list(QuestionQueryRequest request) {
        PageResult<QuestionResponse> result = questionService.list(request);
        return Result.success(result);
    }

    /**
     * 获取题目详情
     */
    @GetMapping("/{id}")
    public Result<QuestionResponse> getById(@PathVariable Long id) {
        QuestionResponse question = questionService.getById(id);
        return Result.success(question);
    }

    /**
     * 创建题目
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody QuestionRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        Long id = questionService.create(request, operator);

        QuestionResponse question = questionService.getById(id);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("questionCode", question.getQuestionCode());

        return Result.success(data);
    }

    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody QuestionRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        questionService.update(id, request, operator);
        return Result.success();
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        questionService.delete(id, operator);
        return Result.success();
    }

    /**
     * 批量删除题目
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        List<Long> ids = request.get("ids");
        questionService.batchDelete(ids, operator);
        return Result.success();
    }

    // ==================== 答案相关接口 ====================

    /**
     * 查询题目下的答案列表
     */
    @GetMapping("/{questionId}/answers")
    public Result<PageResult<AnswerResponse>> listAnswers(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<AnswerResponse> result = questionService.listAnswers(questionId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 创建答案
     */
    @PostMapping("/{questionId}/answers")
    public Result<Map<String, Object>> createAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        Long id = questionService.createAnswer(questionId, request, operator);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);

        return Result.success(data);
    }

    /**
     * 更新答案
     */
    @PutMapping("/answers/{id}")
    public Result<Void> updateAnswer(@PathVariable Long id, @RequestBody AnswerRequest request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        questionService.updateAnswer(id, request, operator);
        return Result.success();
    }

    /**
     * 删除答案
     */
    @DeleteMapping("/answers/{id}")
    public Result<Void> deleteAnswer(@PathVariable Long id) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        questionService.deleteAnswer(id, operator);
        return Result.success();
    }

    /**
     * 批量删除答案
     */
    @PostMapping("/answers/batch-delete")
    public Result<Void> batchDeleteAnswers(@RequestBody Map<String, List<Long>> request) {
        String operator = "admin"; // TODO: 从上下文获取当前用户
        List<Long> ids = request.get("ids");
        questionService.batchDeleteAnswers(ids, operator);
        return Result.success();
    }
}