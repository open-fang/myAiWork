package com.auth.letter.service.impl;

import com.auth.letter.common.PageResult;
import com.auth.letter.dao.RuleParamDao;
import com.auth.letter.dto.request.RuleParamRequest;
import com.auth.letter.entity.RuleParam;
import com.auth.letter.service.RuleParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 规则参数服务实现类
 */
@Service
public class RuleParamServiceImpl implements RuleParamService {

    @Autowired
    private RuleParamDao ruleParamDao;

    @Override
    public PageResult<RuleParam> list(String name, String nameEn, String status, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<RuleParam> list = ruleParamDao.selectList(name, nameEn, status, offset, pageSize);
        long total = ruleParamDao.countList(name, nameEn, status);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public RuleParam getById(Long id) {
        return ruleParamDao.selectById(id);
    }

    @Override
    public List<RuleParam> getAllActive() {
        return ruleParamDao.selectAllActive();
    }

    @Override
    @Transactional
    public Long create(RuleParamRequest request, String operator) {
        // 检查名称是否重复
        List<RuleParam> existing = ruleParamDao.selectList(request.getName(), null, null, 0, 1);
        if (!existing.isEmpty()) {
            throw new RuntimeException("规则参数名称已存在");
        }

        RuleParam ruleParam = new RuleParam();
        ruleParam.setName(request.getName());
        ruleParam.setNameEn(request.getNameEn());
        ruleParam.setBusinessObject(getFirstBusinessObject(request));
        ruleParam.setValueLogic(getFirstValueLogic(request));
        ruleParam.setStatus(request.getStatus());
        ruleParam.setDataType(request.getDataType());
        ruleParam.setCreatedBy(operator);

        ruleParamDao.insert(ruleParam);
        return ruleParam.getId();
    }

    @Override
    @Transactional
    public void update(Long id, RuleParamRequest request, String operator) {
        RuleParam ruleParam = ruleParamDao.selectById(id);
        if (ruleParam == null) {
            throw new RuntimeException("规则参数不存在");
        }

        ruleParam.setName(request.getName());
        ruleParam.setNameEn(request.getNameEn());
        ruleParam.setBusinessObject(getFirstBusinessObject(request));
        ruleParam.setValueLogic(getFirstValueLogic(request));
        ruleParam.setStatus(request.getStatus());
        ruleParam.setDataType(request.getDataType());
        ruleParam.setUpdatedBy(operator);

        ruleParamDao.update(ruleParam);
    }

    @Override
    @Transactional
    public void delete(Long id, String operator) {
        ruleParamDao.deleteById(id);
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, String status, String operator) {
        if (ids != null && !ids.isEmpty()) {
            ruleParamDao.batchUpdateStatus(ids, status, operator);
        }
    }

    private String getFirstBusinessObject(RuleParamRequest request) {
        if (request.getBusinessObjects() != null && !request.getBusinessObjects().isEmpty()) {
            return request.getBusinessObjects().get(0).getBusinessObject();
        }
        return null;
    }

    private String getFirstValueLogic(RuleParamRequest request) {
        if (request.getBusinessObjects() != null && !request.getBusinessObjects().isEmpty()) {
            return request.getBusinessObjects().get(0).getValueLogic();
        }
        return null;
    }
}