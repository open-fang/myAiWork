package com.auth.letter.service;

import com.auth.letter.common.PageResult;
import com.auth.letter.dto.request.AnswerRequest;
import com.auth.letter.dto.request.QuestionQueryRequest;
import com.auth.letter.dto.request.QuestionRequest;
import com.auth.letter.dto.response.AnswerResponse;
import com.auth.letter.dto.response.QuestionResponse;

import java.util.List;

/**
 * 问卷题目服务接口
 */
public interface QuestionService {

    /**
     * 查询题目列表
     */
    PageResult<QuestionResponse> list(QuestionQueryRequest request);

    /**
     * 根据ID查询题目详情
     */
    QuestionResponse getById(Long id);

    /**
     * 创建题目
     */
    Long create(QuestionRequest request, String operator);

    /**
     * 更新题目
     */
    void update(Long id, QuestionRequest request, String operator);

    /**
     * 删除题目
     */
    void delete(Long id, String operator);

    /**
     * 批量删除题目
     */
    void batchDelete(List<Long> ids, String operator);

    /**
     * 查询题目下的答案列表
     */
    PageResult<AnswerResponse> listAnswers(Long questionId, int pageNum, int pageSize);

    /**
     * 创建答案
     */
    Long createAnswer(Long questionId, AnswerRequest request, String operator);

    /**
     * 更新答案
     */
    void updateAnswer(Long id, AnswerRequest request, String operator);

    /**
     * 删除答案
     */
    void deleteAnswer(Long id, String operator);

    /**
     * 批量删除答案
     */
    void batchDeleteAnswers(List<Long> ids, String operator);
}