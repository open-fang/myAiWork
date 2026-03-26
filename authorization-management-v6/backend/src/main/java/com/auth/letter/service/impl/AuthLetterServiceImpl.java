package com.auth.letter.service.impl;

import com.auth.letter.common.Constants;
import com.auth.letter.common.PageResult;
import com.auth.letter.dao.AuthLetterDao;
import com.auth.letter.dao.AuthLetterLogDao;
import com.auth.letter.dto.request.AuthLetterQueryRequest;
import com.auth.letter.dto.request.AuthLetterRequest;
import com.auth.letter.dto.request.BatchRequest;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.AuthLetterLog;
import com.auth.letter.service.AuthLetterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 授权书服务实现类
 */
@Service
public class AuthLetterServiceImpl implements AuthLetterService {

    @Autowired
    private AuthLetterDao authLetterDao;

    @Autowired
    private AuthLetterLogDao authLetterLogDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResult<AuthLetter> list(AuthLetterQueryRequest request) {
        String authObjectLevel = listToJson(request.getAuthObjectLevel());
        String applicableRegion = listToJson(request.getApplicableRegion());
        String authPublishLevel = listToJson(request.getAuthPublishLevel());
        String authPublishOrg = listToJson(request.getAuthPublishOrg());

        List<AuthLetter> list = authLetterDao.selectList(
                request.getName(),
                authObjectLevel,
                applicableRegion,
                authPublishLevel,
                authPublishOrg,
                request.getPublishYear(),
                request.getStatus(),
                request.getOffset(),
                request.getPageSize()
        );

        long total = authLetterDao.countList(
                request.getName(),
                authObjectLevel,
                applicableRegion,
                authPublishLevel,
                authPublishOrg,
                request.getPublishYear(),
                request.getStatus()
        );

        return new PageResult<>(list, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public AuthLetter getById(Long id) {
        return authLetterDao.selectById(id);
    }

    @Override
    @Transactional
    public Long create(AuthLetterRequest request, String operator) {
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName(request.getName());
        authLetter.setAuthObjectLevel(listToJson(request.getAuthObjectLevel()));
        authLetter.setApplicableRegion(listToJson(request.getApplicableRegion()));
        authLetter.setAuthPublishLevel(listToJson(request.getAuthPublishLevel()));
        authLetter.setAuthPublishOrg(listToJson(request.getAuthPublishOrg()));
        authLetter.setPublishYear(request.getPublishYear());
        authLetter.setSummary(request.getSummary());
        authLetter.setStatus(Constants.STATUS_DRAFT);
        authLetter.setCreatedBy(operator);

        authLetterDao.insert(authLetter);

        // 记录日志
        saveLog(authLetter.getId(), Constants.OPERATION_CREATE, operator);

        return authLetter.getId();
    }

    @Override
    @Transactional
    public void update(Long id, AuthLetterRequest request, String operator) {
        AuthLetter authLetter = authLetterDao.selectById(id);
        if (authLetter == null) {
            throw new RuntimeException("授权书不存在");
        }

        if (!Constants.STATUS_DRAFT.equals(authLetter.getStatus())) {
            throw new RuntimeException("只有草稿状态的授权书才能编辑");
        }

        authLetter.setName(request.getName());
        authLetter.setAuthObjectLevel(listToJson(request.getAuthObjectLevel()));
        authLetter.setApplicableRegion(listToJson(request.getApplicableRegion()));
        authLetter.setAuthPublishLevel(listToJson(request.getAuthPublishLevel()));
        authLetter.setAuthPublishOrg(listToJson(request.getAuthPublishOrg()));
        authLetter.setPublishYear(request.getPublishYear());
        authLetter.setSummary(request.getSummary());
        authLetter.setUpdatedBy(operator);

        authLetterDao.update(authLetter);

        // 记录日志
        saveLog(id, Constants.OPERATION_UPDATE, operator);
    }

    @Override
    @Transactional
    public void delete(Long id, String operator) {
        authLetterDao.deleteById(id);
        saveLog(id, Constants.OPERATION_DELETE, operator);
    }

    @Override
    @Transactional
    public void publish(Long id, String operator) {
        AuthLetter authLetter = authLetterDao.selectById(id);
        if (authLetter == null) {
            throw new RuntimeException("授权书不存在");
        }

        if (!Constants.STATUS_DRAFT.equals(authLetter.getStatus())) {
            throw new RuntimeException("只有草稿状态的授权书才能发布");
        }

        authLetterDao.updateStatus(id, Constants.STATUS_PUBLISHED, operator);
        saveLog(id, Constants.OPERATION_PUBLISH, operator);
    }

    @Override
    @Transactional
    public void invalidate(Long id, String operator) {
        AuthLetter authLetter = authLetterDao.selectById(id);
        if (authLetter == null) {
            throw new RuntimeException("授权书不存在");
        }

        if (!Constants.STATUS_PUBLISHED.equals(authLetter.getStatus())) {
            throw new RuntimeException("只有已发布状态的授权书才能失效");
        }

        authLetterDao.updateStatus(id, Constants.STATUS_INVALID, operator);
        saveLog(id, Constants.OPERATION_INVALIDATE, operator);
    }

    @Override
    @Transactional
    public void batchOperation(BatchRequest request, String operator) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return;
        }

        String operation = request.getOperation();
        List<Long> ids = request.getIds();

        switch (operation) {
            case "PUBLISH":
                authLetterDao.batchUpdateStatus(ids, Constants.STATUS_PUBLISHED, operator);
                break;
            case "INVALIDATE":
                authLetterDao.batchUpdateStatus(ids, Constants.STATUS_INVALID, operator);
                break;
            case "DELETE":
                authLetterDao.deleteByIds(ids);
                break;
            default:
                throw new RuntimeException("不支持的操作类型");
        }

        // 记录日志
        for (Long id : ids) {
            saveLog(id, operation, operator);
        }
    }

    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private void saveLog(Long authLetterId, String operation, String operator) {
        AuthLetterLog log = new AuthLetterLog();
        log.setAuthLetterId(authLetterId);
        log.setOperation(operation);
        log.setOperator(operator);
        authLetterLogDao.insert(log);
    }
}