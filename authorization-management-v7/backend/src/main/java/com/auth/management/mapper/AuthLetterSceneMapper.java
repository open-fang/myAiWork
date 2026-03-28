package com.auth.management.mapper;

import com.auth.management.entity.AuthLetterScene;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Authorization Letter Scene Mapper
 */
@Mapper
public interface AuthLetterSceneMapper extends BaseMapper<AuthLetterScene> {

    /**
     * Check if scene name exists under the same auth letter
     */
    Integer checkSceneNameExists(@Param("authLetterId") Long authLetterId,
                                  @Param("sceneName") String sceneName,
                                  @Param("excludeId") Long excludeId);
}