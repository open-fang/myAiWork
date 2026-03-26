package com.auth.letter.service;

import com.auth.letter.common.PageResult;
import com.auth.letter.dto.request.AuthLetterQueryRequest;
import com.auth.letter.dto.request.AuthLetterRequest;
import com.auth.letter.dto.request.BatchRequest;
import com.auth.letter.entity.AuthLetter;

import java.util.List;

/**
 * 授权书服务接口
 */
public interface AuthLetterService {

    /**
     * 查询授权书列表
     */
    PageResult<AuthLetter> list(AuthLetterQueryRequest request);

    /**
     * 根据ID查询授权书
     */
    AuthLetter getById(Long id);

    /**
     * 创建授权书
     */
    Long create(AuthLetterRequest request, String operator);

    /**
     * 更新授权书
     */
    void update(Long id, AuthLetterRequest request, String operator);

    /**
     * 删除授权书
     */
    void delete(Long id, String operator);

    /**
     * 发布授权书
     */
    void publish(Long id, String operator);

    /**
     * 失效授权书
     */
    void invalidate(Long id, String operator);

    /**
     * 批量操作
     */
    void batchOperation(BatchRequest request, String operator);
}