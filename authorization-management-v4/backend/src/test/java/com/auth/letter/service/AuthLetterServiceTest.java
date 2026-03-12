package com.auth.letter.service;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.mapper.AuthLetterMapper;
import com.auth.letter.service.impl.AuthLetterServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthLetterServiceTest {

    @Mock
    private AuthLetterMapper authLetterMapper;

    @Mock
    private com.auth.letter.mapper.AuthSceneMapper authSceneMapper;

    @Mock
    private com.auth.letter.mapper.AuthAttachmentMapper authAttachmentMapper;

    @Mock
    private com.auth.letter.mapper.AuthOperationLogMapper authOperationLogMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuthLetterServiceImpl authLetterService;

    private AuthLetter createTestAuthLetter() {
        AuthLetter letter = new AuthLetter();
        letter.setId(1L);
        letter.setCode("AUTH001");
        letter.setName("测试授权书");
        letter.setStatus("DRAFT");
        letter.setPublishYear(2024);
        letter.setCreatedBy("testuser");
        letter.setCreatedAt(java.time.LocalDateTime.now());
        return letter;
    }

    @Test
    void testQueryList_Success() {
        // Arrange
        AuthLetter letter = createTestAuthLetter();
        when(authLetterMapper.selectByCondition(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(letter));
        when(authLetterMapper.countByCondition(any(), any(), any()))
                .thenReturn(1L);

        AuthLetterQueryDTO queryDTO = new AuthLetterQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // Act
        PageResult<AuthLetterListVO> result = authLetterService.queryList(queryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("测试授权书", result.getList().get(0).getName());
    }

    @Test
    void testGetDetail_Success() {
        // Arrange
        AuthLetter letter = createTestAuthLetter();
        when(authLetterMapper.selectById(1L)).thenReturn(letter);
        when(authSceneMapper.selectByAuthLetterId(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(authAttachmentMapper.selectByAuthLetterId(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(authOperationLogMapper.selectByAuthLetterId(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        // Act
        AuthLetterDetailVO result = authLetterService.getDetail(1L);

        // Assert
        assertNotNull(result);
        assertEquals("测试授权书", result.getName());
        assertEquals("DRAFT", result.getStatus());
    }

    @Test
    void testGetDetail_NotFound() {
        // Arrange
        when(authLetterMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authLetterService.getDetail(999L));
    }

    @Test
    void testCreate_Success() {
        // Arrange
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("新授权书");
        dto.setPublishYear(2024);

        when(authLetterMapper.insert(any(AuthLetter.class))).thenAnswer(invocation -> {
            AuthLetter letter = invocation.getArgument(0);
            letter.setId(1L);
            return 1;
        });

        // Act
        Long id = authLetterService.create(dto);

        // Assert
        assertNotNull(id);
        verify(authLetterMapper, times(1)).insert(any(AuthLetter.class));
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        AuthLetter letter = createTestAuthLetter();
        when(authLetterMapper.selectById(1L)).thenReturn(letter);
        when(authLetterMapper.update(any(AuthLetter.class))).thenReturn(1);

        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("更新后的授权书");

        // Act
        authLetterService.update(1L, dto);

        // Assert
        verify(authLetterMapper, times(1)).update(any(AuthLetter.class));
    }

    @Test
    void testUpdate_NotDraft_ThrowsException() {
        // Arrange
        AuthLetter letter = createTestAuthLetter();
        letter.setStatus("PUBLISHED");
        when(authLetterMapper.selectById(1L)).thenReturn(letter);

        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("更新后的授权书");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authLetterService.update(1L, dto));
    }

    @Test
    void testDelete_Success() {
        // Arrange
        AuthLetter letter = createTestAuthLetter();
        when(authLetterMapper.selectById(1L)).thenReturn(letter);
        when(authLetterMapper.deleteById(1L)).thenReturn(1);

        // Act
        authLetterService.delete(1L);

        // Assert
        verify(authLetterMapper, times(1)).deleteById(1L);
    }

    @Test
    void testPublish_Success() {
        // Arrange
        AuthLetter letter = createTestAuthLetter();
        when(authLetterMapper.selectById(1L)).thenReturn(letter);
        when(authLetterMapper.publish(1L)).thenReturn(1);

        // Act
        authLetterService.publish(1L);

        // Assert
        verify(authLetterMapper, times(1)).publish(1L);
    }

    @Test
    void testExpire_Success() {
        // Arrange
        AuthLetter letter = createTestAuthLetter();
        letter.setStatus("PUBLISHED");
        when(authLetterMapper.selectById(1L)).thenReturn(letter);
        when(authLetterMapper.expire(1L)).thenReturn(1);

        // Act
        authLetterService.expire(1L);

        // Assert
        verify(authLetterMapper, times(1)).expire(1L);
    }

    @Test
    void testBatchPublish_Success() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        when(authLetterMapper.batchPublish(ids)).thenReturn(2);

        // Act
        int count = authLetterService.batchPublish(ids);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testBatchDelete_Success() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        when(authLetterMapper.batchDelete(ids)).thenReturn(2);

        // Act
        int count = authLetterService.batchDelete(ids);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testGetLookupValues_AuthTargetLevel() {
        // Act
        List<LookupValue> result = authLetterService.getLookupValues("AUTH_TARGET_LEVEL");

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertEquals("机关", result.get(0).getName());
    }

    @Test
    void testGetOrgTree_Success() {
        // Act
        Object result = authLetterService.getOrgTree();

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof List);
    }
}