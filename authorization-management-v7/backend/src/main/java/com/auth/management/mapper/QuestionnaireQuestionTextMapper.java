package com.auth.management.mapper;

import com.auth.management.entity.QuestionnaireQuestionText;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Questionnaire Question Text Mapper
 */
@Mapper
public interface QuestionnaireQuestionTextMapper extends BaseMapper<QuestionnaireQuestionText> {

    /**
     * Select by question id
     */
    List<QuestionnaireQuestionText> selectByQuestionId(@Param("questionId") Long questionId);

    /**
     * Delete by question id
     */
    Integer deleteByQuestionId(@Param("questionId") Long questionId);

    /**
     * Check if language exists for the question
     */
    Integer checkLanguageExists(@Param("questionId") Long questionId,
                                 @Param("language") String language,
                                 @Param("excludeId") Long excludeId);
}