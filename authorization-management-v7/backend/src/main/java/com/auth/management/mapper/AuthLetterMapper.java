package com.auth.management.mapper;

import com.auth.management.entity.AuthLetter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Authorization Letter Mapper
 */
@Mapper
public interface AuthLetterMapper extends BaseMapper<AuthLetter> {

    /**
     * Select auth letter list with conditions
     */
    List<AuthLetter> selectListWithConditions(Map<String, Object> params);

    /**
     * Count auth letter list with conditions
     */
    Long countListWithConditions(Map<String, Object> params);

    /**
     * Check if name exists
     */
    Integer checkNameExists(@Param("name") String name, @Param("excludeId") Long excludeId);
}