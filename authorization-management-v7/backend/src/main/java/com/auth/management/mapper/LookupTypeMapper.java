package com.auth.management.mapper;

import com.auth.management.entity.LookupType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Lookup Type Mapper
 */
@Mapper
public interface LookupTypeMapper extends BaseMapper<LookupType> {

    /**
     * Select by type code
     */
    LookupType selectByTypeCode(@Param("typeCode") String typeCode);
}