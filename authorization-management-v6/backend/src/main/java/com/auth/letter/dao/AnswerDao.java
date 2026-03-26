package com.auth.letter.dao;

import com.auth.letter.entity.Answer;
import com.auth.letter.entity.AnswerText;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问卷答案DAO接口
 */
public interface AnswerDao {

    /**
     * 查询答案列表
     */
    List<Answer> selectByQuestionId(@Param("questionId") Long questionId,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    /**
     * 查询答案总数
     */
    long countByQuestionId(@Param("questionId") Long questionId);

    /**
     * 根据ID查询答案
     */
    Answer selectById(@Param("id") Long id);

    /**
     * 插入答案
     */
    int insert(Answer answer);

    /**
     * 更新答案
     */
    int update(Answer answer);

    /**
     * 删除答案（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除答案
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 删除题目下的所有答案
     */
    int deleteByQuestionId(@Param("questionId") Long questionId);

    /**
     * 查询答案文本列表
     */
    List<AnswerText> selectTextsByAnswerId(@Param("answerId") Long answerId);

    /**
     * 插入答案文本
     */
    int insertText(AnswerText answerText);

    /**
     * 更新答案文本
     */
    int updateText(AnswerText answerText);

    /**
     * 删除答案的所有文本
     */
    int deleteTextsByAnswerId(@Param("answerId") Long answerId);

    /**
     * 检查答案是否存在相同语言的文本
     */
    int countTextByLanguage(@Param("answerId") Long answerId,
                            @Param("language") String language,
                            @Param("excludeId") Long excludeId);
}