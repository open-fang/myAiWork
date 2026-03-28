package com.auth.management.repository;

import com.auth.management.entity.RuleParam;
import com.auth.management.mapper.RuleParamMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Rule Parameter Repository
 */
@Repository
public class RuleParamRepository {

    @Autowired
    private RuleParamMapper ruleParamMapper;

    public RuleParam findById(Long id) {
        return ruleParamMapper.selectById(id);
    }

    public Page<RuleParam> findPage(Page<RuleParam> page, String name, String nameEn, String status) {
        LambdaQueryWrapper<RuleParam> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(RuleParam::getName, name);
        }
        if (nameEn != null && !nameEn.isEmpty()) {
            wrapper.like(RuleParam::getNameEn, nameEn);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(RuleParam::getStatus, status);
        }
        wrapper.orderByDesc(RuleParam::getCreatedTime);
        return ruleParamMapper.selectPage(page, wrapper);
    }

    public List<RuleParam> findByStatus(String status) {
        LambdaQueryWrapper<RuleParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RuleParam::getStatus, status);
        wrapper.orderByAsc(RuleParam::getName);
        return ruleParamMapper.selectList(wrapper);
    }

    public Long insert(RuleParam ruleParam) {
        ruleParamMapper.insert(ruleParam);
        return ruleParam.getId();
    }

    public void update(RuleParam ruleParam) {
        ruleParamMapper.updateById(ruleParam);
    }

    public void deleteById(Long id) {
        ruleParamMapper.deleteById(id);
    }

    public void batchUpdateStatus(List<Long> ids, String status) {
        for (Long id : ids) {
            RuleParam ruleParam = ruleParamMapper.selectById(id);
            if (ruleParam != null) {
                ruleParam.setStatus(status);
                ruleParamMapper.updateById(ruleParam);
            }
        }
    }

    public boolean checkNameExists(String name, Long excludeId) {
        return ruleParamMapper.checkNameExists(name, excludeId) > 0;
    }

    public boolean checkNameEnExists(String nameEn, Long excludeId) {
        return ruleParamMapper.checkNameEnExists(nameEn, excludeId) > 0;
    }
}