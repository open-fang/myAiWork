package com.auth.management.repository;

import com.auth.management.entity.AuthLetter;
import com.auth.management.entity.AuthLetterLog;
import com.auth.management.mapper.AuthLetterLogMapper;
import com.auth.management.mapper.AuthLetterMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Authorization Letter Repository
 */
@Repository
public class AuthLetterRepository {

    @Autowired
    private AuthLetterMapper authLetterMapper;

    @Autowired
    private AuthLetterLogMapper authLetterLogMapper;

    public AuthLetter findById(Long id) {
        return authLetterMapper.selectById(id);
    }

    public List<AuthLetter> findByIds(List<Long> ids) {
        return authLetterMapper.selectBatchIds(ids);
    }

    public Page<AuthLetter> findPage(Page<AuthLetter> page, Map<String, Object> params) {
        params.put("offset", (page.getCurrent() - 1) * page.getSize());
        params.put("pageSize", page.getSize());
        List<AuthLetter> records = authLetterMapper.selectListWithConditions(params);
        Long total = authLetterMapper.countListWithConditions(params);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    public Long insert(AuthLetter authLetter) {
        authLetterMapper.insert(authLetter);
        return authLetter.getId();
    }

    public void update(AuthLetter authLetter) {
        authLetterMapper.updateById(authLetter);
    }

    public void delete(Long id) {
        authLetterMapper.deleteById(id);
    }

    public boolean checkNameExists(String name, Long excludeId) {
        return authLetterMapper.checkNameExists(name, excludeId) > 0;
    }

    // Log methods
    public void insertLog(AuthLetterLog log) {
        authLetterLogMapper.insert(log);
    }

    public Page<AuthLetterLog> findLogPage(Page<AuthLetterLog> page, Long authLetterId) {
        LambdaQueryWrapper<AuthLetterLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthLetterLog::getAuthLetterId, authLetterId);
        wrapper.orderByDesc(AuthLetterLog::getOperationTime);
        return authLetterLogMapper.selectPage(page, wrapper);
    }
}