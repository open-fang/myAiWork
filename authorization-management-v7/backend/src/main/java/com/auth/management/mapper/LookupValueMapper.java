package com.auth.management.mapper;

import com.auth.management.entity.LookupValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Lookup Value Mapper
 */
@Mapper
public interface LookupValueMapper extends BaseMapper<LookupValue> {

    /**
     * Select by type id (flat list)
     */
    List<LookupValue> selectByTypeId(@Param("typeId") Long typeId);

    /**
     * Select by type id with tree structure (recursive)
     */
    List<LookupValue> selectTreeByTypeId(@Param("typeId") Long typeId);

    /**
     * Select children by parent code
     */
    List<LookupValue> selectChildrenByParentCode(@Param("parentCode") String parentCode);
}