package com.auth.management.mapper;

import com.auth.management.entity.QuestionnaireAnswerText;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Questionnaire Answer Text Mapper
 */
@Mapper
public interface QuestionnaireAnswerTextMapper extends BaseMapper<QuestionnaireAnswerText> {

    /**
     * Select by answer id
     */
    List<QuestionnaireAnswerText> selectByAnswerId(@Param("answerId") Long answerId);

    /**
     * Delete by answer id
     */
    Integer deleteByAnswerId(@Param("answerId") Long answerId);

    /**
     * Delete by answer ids
     */
    Integer deleteByAnswerIds(@Param("answerIds") List<Long> answerIds);
}