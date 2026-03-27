package com.auth.letter.service;

import com.auth.letter.common.Constants;
import com.auth.letter.common.PageResult;
import com.auth.letter.dao.AuthLetterDao;
import com.auth.letter.dao.AuthLetterLogDao;
import com.auth.letter.dto.request.AuthLetterQueryRequest;
import com.auth.letter.dto.request.AuthLetterRequest;
import com.auth.letter.dto.request.BatchRequest;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.AuthLetterLog;
import com.auth.letter.service.impl.AuthLetterServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 授权书服务测试类
 */
@ExtendWith(MockitoExtension.class)
class AuthLetterServiceTest {

    @Mock
    private AuthLetterDao authLetterDao;

    @Mock
    private AuthLetterLogDao authLetterLogDao;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthLetterServiceImpl authLetterService;

    private AuthLetter testAuthLetter;
    private AuthLetterRequest testRequest;

    @BeforeEach
    void setUp() {
        testAuthLetter = new AuthLetter();
        testAuthLetter.setId(1L);
        testAuthLetter.setName("测试授权书");
        testAuthLetter.setStatus(Constants.STATUS_DRAFT);
        testAuthLetter.setDeleteFlag(0);

        testRequest = new AuthLetterRequest();
        testRequest.setName("测试授权书");
        testRequest.setPublishYear(2026);
        testRequest.setSummary("测试摘要");
    }

    // ==================== 查询测试 ====================

    @Test
    @DisplayName("查询授权书列表 - 成功")
    void testList_Success() {
        // Given
        AuthLetterQueryRequest request = new AuthLetterQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        when(authLetterDao.selectList(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(testAuthLetter));
        when(authLetterDao.countList(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1L);

        // When
        PageResult<AuthLetter> result = authLetterService.list(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("测试授权书", result.getList().get(0).getName());
    }

    @Test
    @DisplayName("根据ID查询授权书 - 成功")
    void testGetById_Success() {
        // Given
        when(authLetterDao.selectById(1L)).thenReturn(testAuthLetter);

        // When
        AuthLetter result = authLetterService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试授权书", result.getName());
    }

    @Test
    @DisplayName("根据ID查询授权书 - 不存在")
    void testGetById_NotFound() {
        // Given
        when(authLetterDao.selectById(999L)).thenReturn(null);

        // When
        AuthLetter result = authLetterService.getById(999L);

        // Then
        assertNull(result);
    }

    // ==================== 创建测试 ====================

    @Test
    @DisplayName("创建授权书 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        when(authLetterDao.insert(any(AuthLetter.class))).thenAnswer(invocation -> {
            AuthLetter letter = invocation.getArgument(0);
            letter.setId(1L);
            return 1;
        });
        when(authLetterLogDao.insert(any(AuthLetterLog.class))).thenReturn(1);
        lenient().when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        // When
        Long id = authLetterService.create(testRequest, "admin");

        // Then
        assertNotNull(id);
        assertEquals(1L, id);
        verify(authLetterDao).insert(argThat(letter ->
                letter.getName().equals("测试授权书") &&
                letter.getStatus().equals(Constants.STATUS_DRAFT) &&
                letter.getCreatedBy().equals("admin")
        ));
        verify(authLetterLogDao).insert(any(AuthLetterLog.class));
    }

    // ==================== 更新测试 ====================

    @Test
    @DisplayName("更新授权书 - 成功")
    void testUpdate_Success() throws Exception {
        // Given
        when(authLetterDao.selectById(1L)).thenReturn(testAuthLetter);
        when(authLetterDao.update(any(AuthLetter.class))).thenReturn(1);
        when(authLetterLogDao.insert(any(AuthLetterLog.class))).thenReturn(1);
        lenient().when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        // When
        authLetterService.update(1L, testRequest, "admin");

        // Then
        verify(authLetterDao).update(argThat(letter ->
                letter.getName().equals("测试授权书") &&
                letter.getUpdatedBy().equals("admin")
        ));
        verify(authLetterLogDao).insert(any(AuthLetterLog.class));
    }

    @Test
    @DisplayName("更新授权书 - 不存在")
    void testUpdate_NotFound() {
        // Given
        when(authLetterDao.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                authLetterService.update(999L, testRequest, "admin")
        );
    }

    @Test
    @DisplayName("更新授权书 - 状态不是草稿")
    void testUpdate_NotDraft() {
        // Given
        testAuthLetter.setStatus(Constants.STATUS_PUBLISHED);
        when(authLetterDao.selectById(1L)).thenReturn(testAuthLetter);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authLetterService.update(1L, testRequest, "admin")
        );
        assertEquals("只有草稿状态的授权书才能编辑", exception.getMessage());
    }

    // ==================== 删除测试 ====================

    @Test
    @DisplayName("删除授权书 - 成功")
    void testDelete_Success() {
        // Given
        when(authLetterDao.deleteById(1L)).thenReturn(1);
        when(authLetterLogDao.insert(any(AuthLetterLog.class))).thenReturn(1);

        // When
        authLetterService.delete(1L, "admin");

        // Then
        verify(authLetterDao).deleteById(1L);
        verify(authLetterLogDao).insert(any(AuthLetterLog.class));
    }

    // ==================== 发布测试 ====================

    @Test
    @DisplayName("发布授权书 - 成功")
    void testPublish_Success() {
        // Given
        when(authLetterDao.selectById(1L)).thenReturn(testAuthLetter);
        when(authLetterDao.updateStatus(1L, Constants.STATUS_PUBLISHED, "admin")).thenReturn(1);
        when(authLetterLogDao.insert(any(AuthLetterLog.class))).thenReturn(1);

        // When
        authLetterService.publish(1L, "admin");

        // Then
        verify(authLetterDao).updateStatus(1L, Constants.STATUS_PUBLISHED, "admin");
        verify(authLetterLogDao).insert(any(AuthLetterLog.class));
    }

    @Test
    @DisplayName("发布授权书 - 不存在")
    void testPublish_NotFound() {
        // Given
        when(authLetterDao.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                authLetterService.publish(999L, "admin")
        );
    }

    @Test
    @DisplayName("发布授权书 - 状态不是草稿")
    void testPublish_NotDraft() {
        // Given
        testAuthLetter.setStatus(Constants.STATUS_PUBLISHED);
        when(authLetterDao.selectById(1L)).thenReturn(testAuthLetter);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authLetterService.publish(1L, "admin")
        );
        assertEquals("只有草稿状态的授权书才能发布", exception.getMessage());
    }

    // ==================== 失效测试 ====================

    @Test
    @DisplayName("失效授权书 - 成功")
    void testInvalidate_Success() {
        // Given
        testAuthLetter.setStatus(Constants.STATUS_PUBLISHED);
        when(authLetterDao.selectById(1L)).thenReturn(testAuthLetter);
        when(authLetterDao.updateStatus(1L, Constants.STATUS_INVALID, "admin")).thenReturn(1);
        when(authLetterLogDao.insert(any(AuthLetterLog.class))).thenReturn(1);

        // When
        authLetterService.invalidate(1L, "admin");

        // Then
        verify(authLetterDao).updateStatus(1L, Constants.STATUS_INVALID, "admin");
        verify(authLetterLogDao).insert(any(AuthLetterLog.class));
    }

    @Test
    @DisplayName("失效授权书 - 状态不是已发布")
    void testInvalidate_NotPublished() {
        // Given
        testAuthLetter.setStatus(Constants.STATUS_DRAFT);
        when(authLetterDao.selectById(1L)).thenReturn(testAuthLetter);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authLetterService.invalidate(1L, "admin")
        );
        assertEquals("只有已发布状态的授权书才能失效", exception.getMessage());
    }

    // ==================== 批量操作测试 ====================

    @Test
    @DisplayName("批量发布 - 成功")
    void testBatchOperation_Publish() {
        // Given
        BatchRequest request = new BatchRequest();
        request.setIds(Arrays.asList(1L, 2L));
        request.setOperation("PUBLISH");

        when(authLetterDao.batchUpdateStatus(anyList(), eq(Constants.STATUS_PUBLISHED), anyString()))
                .thenReturn(2);
        when(authLetterLogDao.insert(any(AuthLetterLog.class))).thenReturn(1);

        // When
        authLetterService.batchOperation(request, "admin");

        // Then
        verify(authLetterDao).batchUpdateStatus(
                eq(Arrays.asList(1L, 2L)),
                eq(Constants.STATUS_PUBLISHED),
                eq("admin")
        );
        verify(authLetterLogDao, times(2)).insert(any(AuthLetterLog.class));
    }

    @Test
    @DisplayName("批量删除 - 成功")
    void testBatchOperation_Delete() {
        // Given
        BatchRequest request = new BatchRequest();
        request.setIds(Arrays.asList(1L, 2L));
        request.setOperation("DELETE");

        when(authLetterDao.deleteByIds(anyList())).thenReturn(2);
        when(authLetterLogDao.insert(any(AuthLetterLog.class))).thenReturn(1);

        // When
        authLetterService.batchOperation(request, "admin");

        // Then
        verify(authLetterDao).deleteByIds(eq(Arrays.asList(1L, 2L)));
        verify(authLetterLogDao, times(2)).insert(any(AuthLetterLog.class));
    }

    @Test
    @DisplayName("批量操作 - 不支持的操作类型")
    void testBatchOperation_UnsupportedOperation() {
        // Given
        BatchRequest request = new BatchRequest();
        request.setIds(Arrays.asList(1L));
        request.setOperation("UNSUPPORTED");

        // When & Then
        assertThrows(RuntimeException.class, () ->
                authLetterService.batchOperation(request, "admin")
        );
    }

    @Test
    @DisplayName("批量操作 - 空ID列表")
    void testBatchOperation_EmptyIds() {
        // Given
        BatchRequest request = new BatchRequest();
        request.setIds(Collections.emptyList());
        request.setOperation("PUBLISH");

        // When
        authLetterService.batchOperation(request, "admin");

        // Then
        verify(authLetterDao, never()).batchUpdateStatus(anyList(), anyString(), anyString());
    }
}