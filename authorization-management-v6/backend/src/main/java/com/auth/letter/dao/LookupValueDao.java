package com.auth.letter.dao;

import com.auth.letter.entity.LookupValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 下拉值DAO接口
 */
public interface LookupValueDao {

    /**
     * 根据类型编码查询下拉值列表
     */
    List<LookupValue> selectByTypeCode(@Param("typeCode") String typeCode);

    /**
     * 根据类型ID查询下拉值列表
     */
    List<LookupValue> selectByTypeId(@Param("typeId") Long typeId);

    /**
     * 根据父节点编码查询子节点
     */
    List<LookupValue> selectByParentCode(@Param("typeCode") String typeCode, @Param("parentCode") String parentCode);
}