package com.auth.management.mapper;

import com.auth.management.entity.QuestionnaireAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Questionnaire Answer Mapper
 */
@Mapper
public interface QuestionnaireAnswerMapper extends BaseMapper<QuestionnaireAnswer> {

    /**
     * Select by question id
     */
    List<QuestionnaireAnswer> selectByQuestionId(@Param("questionId") Long questionId);

    /**
     * Delete by question id
     */
    Integer deleteByQuestionId(@Param("questionId") Long questionId);
}