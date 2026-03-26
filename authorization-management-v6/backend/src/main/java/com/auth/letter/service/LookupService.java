package com.auth.letter.service;

import com.auth.letter.entity.LookupValue;

import java.util.List;

/**
 * 下拉值服务接口
 */
public interface LookupService {

    /**
     * 根据类型编码获取下拉值列表（平铺格式）
     */
    List<LookupValue> getFlatList(String typeCode);

    /**
     * 根据类型编码获取下拉值树形结构
     */
    List<LookupValue> getTreeList(String typeCode);
}