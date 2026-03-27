package com.auth.letter.service;

import com.auth.letter.common.PageResult;
import com.auth.letter.dao.AnswerDao;
import com.auth.letter.dao.QuestionDao;
import com.auth.letter.dto.request.AnswerRequest;
import com.auth.letter.dto.request.AnswerTextRequest;
import com.auth.letter.dto.request.QuestionQueryRequest;
import com.auth.letter.dto.request.QuestionRequest;
import com.auth.letter.dto.request.QuestionTextRequest;
import com.auth.letter.dto.response.AnswerResponse;
import com.auth.letter.dto.response.QuestionResponse;
import com.auth.letter.entity.Answer;
import com.auth.letter.entity.Question;
import com.auth.letter.service.impl.QuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 问卷题目服务测试类
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionDao questionDao;

    @Mock
    private AnswerDao answerDao;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Question testQuestion;
    private QuestionRequest testQuestionRequest;

    @BeforeEach
    void setUp() {
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setQuestionCode("QT20260326001");
        testQuestion.setStatus("ACTIVE");
        testQuestion.setCreatedBy("admin");

        testQuestionRequest = new QuestionRequest();
        testQuestionRequest.setStatus("ACTIVE");

        QuestionTextRequest textRequest = new QuestionTextRequest();
        textRequest.setLanguage("ZH");
        textRequest.setQuestionText("测试题目");
        testQuestionRequest.setQuestionTexts(Collections.singletonList(textRequest));
    }

    // ==================== 题目列表查询测试 ====================

    @Test
    @DisplayName("查询题目列表 - 成功")
    void testList_Success() {
        // Given
        QuestionQueryRequest request = new QuestionQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        when(questionDao.selectList(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(testQuestion));
        when(questionDao.countList(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1L);
        when(questionDao.selectTextsByQuestionId(anyLong()))
                .thenReturn(Collections.emptyList());

        // When
        PageResult<QuestionResponse> result = questionService.list(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
    }

    @Test
    @DisplayName("查询题目列表 - 按条件查询")
    void testList_WithCondition() {
        // Given
        QuestionQueryRequest request = new QuestionQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        request.setQuestionCode("QT");
        request.setLanguage("ZH");

        when(questionDao.selectList(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(testQuestion));
        when(questionDao.countList(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1L);
        when(questionDao.selectTextsByQuestionId(anyLong()))
                .thenReturn(Collections.emptyList());

        // When
        PageResult<QuestionResponse> result = questionService.list(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    // ==================== 创建题目测试 ====================

    @Test
    @DisplayName("创建题目 - 成功")
    void testCreate_Success() {
        // Given
        when(questionDao.countTextByLanguage(any(), anyString(), any())).thenReturn(0);
        when(questionDao.insert(any(Question.class))).thenAnswer(invocation -> {
            Question q = invocation.getArgument(0);
            q.setId(1L);
            return 1;
        });
        when(questionDao.insertText(any())).thenReturn(1);

        // When
        Long id = questionService.create(testQuestionRequest, "admin");

        // Then
        assertNotNull(id);
        verify(questionDao).insert(any(Question.class));
        verify(questionDao).insertText(any());
    }

    @Test
    @DisplayName("创建题目 - 语言重复失败")
    void testCreate_DuplicateLanguage() {
        // Given
        when(questionDao.countTextByLanguage(anyLong(), anyString(), any())).thenReturn(1);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                questionService.create(testQuestionRequest, "admin")
        );
    }

    // ==================== 更新题目测试 ====================

    @Test
    @DisplayName("更新题目 - 成功")
    void testUpdate_Success() {
        // Given
        when(questionDao.selectById(1L)).thenReturn(testQuestion);
        when(questionDao.countTextByLanguage(anyLong(), anyString(), any())).thenReturn(0);
        when(questionDao.update(any(Question.class))).thenReturn(1);
        when(questionDao.deleteTextsByQuestionId(anyLong())).thenReturn(1);
        when(questionDao.insertText(any())).thenReturn(1);

        // When
        questionService.update(1L, testQuestionRequest, "admin");

        // Then
        verify(questionDao).update(any(Question.class));
        verify(questionDao).deleteTextsByQuestionId(1L);
        verify(questionDao).insertText(any());
    }

    @Test
    @DisplayName("更新题目 - 题目不存在")
    void testUpdate_NotFound() {
        // Given
        when(questionDao.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                questionService.update(999L, testQuestionRequest, "admin")
        );
    }

    // ==================== 删除题目测试 ====================

    @Test
    @DisplayName("删除题目 - 成功")
    void testDelete_Success() {
        // Given
        when(answerDao.countByQuestionId(1L)).thenReturn(0L);
        when(questionDao.deleteTextsByQuestionId(1L)).thenReturn(1);
        when(questionDao.deleteById(1L)).thenReturn(1);

        // When
        questionService.delete(1L, "admin");

        // Then
        verify(questionDao).deleteTextsByQuestionId(1L);
        verify(questionDao).deleteById(1L);
    }

    @Test
    @DisplayName("删除题目 - 存在答案时失败")
    void testDelete_HasAnswers() {
        // Given
        when(answerDao.countByQuestionId(1L)).thenReturn(5L);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                questionService.delete(1L, "admin")
        );
    }

    @Test
    @DisplayName("批量删除题目 - 成功")
    void testBatchDelete_Success() {
        // Given
        when(answerDao.countByQuestionId(anyLong())).thenReturn(0L);
        when(questionDao.deleteTextsByQuestionId(anyLong())).thenReturn(1);
        when(questionDao.deleteByIds(anyList())).thenReturn(2);

        // When
        questionService.batchDelete(Arrays.asList(1L, 2L), "admin");

        // Then
        verify(questionDao, times(2)).deleteTextsByQuestionId(anyLong());
        verify(questionDao).deleteByIds(anyList());
    }

    // ==================== 答案相关测试 ====================

    @Test
    @DisplayName("查询答案列表 - 成功")
    void testListAnswers_Success() {
        // Given
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setAnswerCode("ANS001");
        answer.setQuestionId(1L);

        when(answerDao.selectByQuestionId(eq(1L), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(answer));
        when(answerDao.countByQuestionId(1L)).thenReturn(1L);
        when(answerDao.selectTextsByAnswerId(anyLong()))
                .thenReturn(Collections.emptyList());

        // When
        PageResult<AnswerResponse> result = questionService.listAnswers(1L, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("创建答案 - 成功")
    void testCreateAnswer_Success() {
        // Given
        AnswerRequest request = new AnswerRequest();
        AnswerTextRequest textRequest = new AnswerTextRequest();
        textRequest.setLanguage("ZH");
        textRequest.setAnswerText("选项A");
        request.setAnswerTexts(Collections.singletonList(textRequest));
        request.setSortOrder(1);

        when(questionDao.selectById(1L)).thenReturn(testQuestion);
        when(answerDao.insert(any(Answer.class))).thenAnswer(invocation -> {
            Answer a = invocation.getArgument(0);
            a.setId(1L);
            return 1;
        });
        when(answerDao.insertText(any())).thenReturn(1);

        // When
        Long id = questionService.createAnswer(1L, request, "admin");

        // Then
        assertNotNull(id);
        verify(answerDao).insert(any(Answer.class));
    }

    @Test
    @DisplayName("创建答案 - 题目不存在")
    void testCreateAnswer_QuestionNotFound() {
        // Given
        AnswerRequest request = new AnswerRequest();
        when(questionDao.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                questionService.createAnswer(999L, request, "admin")
        );
    }

    @Test
    @DisplayName("删除答案 - 成功")
    void testDeleteAnswer_Success() {
        // Given
        when(answerDao.deleteTextsByAnswerId(1L)).thenReturn(1);
        when(answerDao.deleteById(1L)).thenReturn(1);

        // When
        questionService.deleteAnswer(1L, "admin");

        // Then
        verify(answerDao).deleteTextsByAnswerId(1L);
        verify(answerDao).deleteById(1L);
    }

    @Test
    @DisplayName("批量删除答案 - 成功")
    void testBatchDeleteAnswers_Success() {
        // Given
        when(answerDao.deleteTextsByAnswerId(anyLong())).thenReturn(1);
        when(answerDao.deleteByIds(anyList())).thenReturn(2);

        // When
        questionService.batchDeleteAnswers(Arrays.asList(1L, 2L), "admin");

        // Then
        verify(answerDao, times(2)).deleteTextsByAnswerId(anyLong());
        verify(answerDao).deleteByIds(anyList());
    }
}