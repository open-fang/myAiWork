package com.auth.letter.service.impl;

import com.auth.letter.entity.LookupValue;
import com.auth.letter.dao.LookupValueDao;
import com.auth.letter.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下拉值服务实现类
 */
@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    private LookupValueDao lookupValueDao;

    @Override
    public List<LookupValue> getFlatList(String typeCode) {
        return lookupValueDao.selectByTypeCode(typeCode);
    }

    @Override
    public List<LookupValue> getTreeList(String typeCode) {
        List<LookupValue> allValues = lookupValueDao.selectByTypeCode(typeCode);
        return buildTree(allValues, null);
    }

    private List<LookupValue> buildTree(List<LookupValue> allValues, String parentCode) {
        List<LookupValue> result = new ArrayList<>();
        for (LookupValue value : allValues) {
            boolean isMatch = (parentCode == null && value.getParentCode() == null) ||
                    (parentCode != null && parentCode.equals(value.getParentCode()));
            if (isMatch) {
                // 这里不实际设置children，由前端处理
                result.add(value);
            }
        }
        return result;
    }
}