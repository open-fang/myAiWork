package com.auth.management.mapper;

import com.auth.management.entity.RuleParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Rule Parameter Mapper
 */
@Mapper
public interface RuleParamMapper extends BaseMapper<RuleParam> {

    /**
     * Check if name exists
     */
    Integer checkNameExists(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * Check if name en exists
     */
    Integer checkNameEnExists(@Param("nameEn") String nameEn, @Param("excludeId") Long excludeId);
}