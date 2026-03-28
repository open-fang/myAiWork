package com.auth.management.repository;

import com.auth.management.entity.LookupType;
import com.auth.management.entity.LookupValue;
import com.auth.management.mapper.LookupTypeMapper;
import com.auth.management.mapper.LookupValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Lookup Repository
 */
@Repository
public class LookupRepository {

    @Autowired
    private LookupTypeMapper lookupTypeMapper;

    @Autowired
    private LookupValueMapper lookupValueMapper;

    public LookupType findByTypeCode(String typeCode) {
        return lookupTypeMapper.selectByTypeCode(typeCode);
    }

    public List<LookupValue> findByTypeId(Long typeId) {
        return lookupValueMapper.selectByTypeId(typeId);
    }

    public List<LookupValue> findTreeByTypeId(Long typeId) {
        return lookupValueMapper.selectTreeByTypeId(typeId);
    }

    public List<LookupValue> findChildrenByParentCode(String parentCode) {
        return lookupValueMapper.selectChildrenByParentCode(parentCode);
    }
}