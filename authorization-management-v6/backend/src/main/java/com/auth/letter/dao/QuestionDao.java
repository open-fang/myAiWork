package com.auth.letter.dao;

import com.auth.letter.entity.Question;
import com.auth.letter.entity.QuestionText;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问卷题目DAO接口
 */
public interface QuestionDao {

    /**
     * 查询题目列表
     */
    List<Question> selectList(@Param("questionCode") String questionCode,
                              @Param("questionText") String questionText,
                              @Param("language") String language,
                              @Param("createdBy") String createdBy,
                              @Param("createdDate") String createdDate,
                              @Param("updatedBy") String updatedBy,
                              @Param("updatedDate") String updatedDate,
                              @Param("offset") int offset,
                              @Param("pageSize") int pageSize);

    /**
     * 查询题目总数
     */
    long countList(@Param("questionCode") String questionCode,
                   @Param("questionText") String questionText,
                   @Param("language") String language,
                   @Param("createdBy") String createdBy,
                   @Param("createdDate") String createdDate,
                   @Param("updatedBy") String updatedBy,
                   @Param("updatedDate") String updatedDate);

    /**
     * 根据ID查询题目
     */
    Question selectById(@Param("id") Long id);

    /**
     * 插入题目
     */
    int insert(Question question);

    /**
     * 更新题目
     */
    int update(Question question);

    /**
     * 删除题目（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除题目
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 查询题目文本列表
     */
    List<QuestionText> selectTextsByQuestionId(@Param("questionId") Long questionId);

    /**
     * 插入题目文本
     */
    int insertText(QuestionText questionText);

    /**
     * 更新题目文本
     */
    int updateText(QuestionText questionText);

    /**
     * 删除题目的所有文本
     */
    int deleteTextsByQuestionId(@Param("questionId") Long questionId);

    /**
     * 检查题目是否存在相同语言的文本
     */
    int countTextByLanguage(@Param("questionId") Long questionId,
                            @Param("language") String language,
                            @Param("excludeId") Long excludeId);
}