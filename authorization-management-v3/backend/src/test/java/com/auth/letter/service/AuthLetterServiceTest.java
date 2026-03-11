package com.auth.letter.service;

import com.auth.letter.dto.AuthLetterDetailDTO;
import com.auth.letter.dto.AuthLetterDetailVO;
import com.auth.letter.dto.AuthLetterListVO;
import com.auth.letter.dto.AuthLetterQueryDTO;
import com.auth.letter.dto.PageResult;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.service.impl.AuthLetterServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthLetterService Tests
 */
@ExtendWith(MockitoExtension.class)
class AuthLetterServiceTest {

    @Mock
    private AuthLetterRepository authLetterRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuthLetterServiceImpl authLetterService;

    private AuthLetter testAuthLetter;

    @BeforeEach
    void setUp() {
        testAuthLetter = new AuthLetter();
        testAuthLetter.setId(1L);
        testAuthLetter.setCode("AUTH001");
        testAuthLetter.setName("测试授权书");
        testAuthLetter.setStatus(AuthLetterStatus.DRAFT);
        testAuthLetter.setPublishYear(2024);
        testAuthLetter.setCreatedBy("admin");
    }

    @Test
    void testQueryList() {
        // Arrange
        Page<AuthLetter> page = new PageImpl<>(Arrays.asList(testAuthLetter));
        when(authLetterRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

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
    void testGetDetail() {
        // Arrange
        when(authLetterRepository.findById(1L)).thenReturn(Optional.of(testAuthLetter));

        // Act
        AuthLetterDetailVO result = authLetterService.getDetail(1L);

        // Assert
        assertNotNull(result);
        assertEquals("测试授权书", result.getName());
        assertEquals("DRAFT", result.getStatus());
    }

    @Test
    void testGetDetailNotFound() {
        // Arrange
        when(authLetterRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authLetterService.getDetail(999L));
    }

    @Test
    void testCreate() {
        // Arrange
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("新授权书");
        dto.setPublishYear(2024);

        when(authLetterRepository.save(any(AuthLetter.class))).thenAnswer(invocation -> {
            AuthLetter saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        // Act
        Long id = authLetterService.create(dto);

        // Assert
        assertNotNull(id);
        verify(authLetterRepository, times(1)).save(any(AuthLetter.class));
    }

    @Test
    void testUpdate() {
        // Arrange
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("更新后的名称");
        dto.setPublishYear(2025);

        when(authLetterRepository.findById(1L)).thenReturn(Optional.of(testAuthLetter));
        when(authLetterRepository.save(any(AuthLetter.class))).thenReturn(testAuthLetter);

        // Act
        authLetterService.update(1L, dto);

        // Assert
        verify(authLetterRepository, times(1)).save(any(AuthLetter.class));
    }

    @Test
    void testUpdateNonDraftStatus() {
        // Arrange
        testAuthLetter.setStatus(AuthLetterStatus.PUBLISHED);
        when(authLetterRepository.findById(1L)).thenReturn(Optional.of(testAuthLetter));

        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("更新后的名称");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authLetterService.update(1L, dto));
    }

    @Test
    void testDelete() {
        // Arrange
        when(authLetterRepository.findById(1L)).thenReturn(Optional.of(testAuthLetter));
        doNothing().when(authLetterRepository).deleteById(1L);

        // Act
        authLetterService.delete(1L);

        // Assert
        verify(authLetterRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNonDraftStatus() {
        // Arrange
        testAuthLetter.setStatus(AuthLetterStatus.PUBLISHED);
        when(authLetterRepository.findById(1L)).thenReturn(Optional.of(testAuthLetter));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authLetterService.delete(1L));
    }

    @Test
    void testPublish() {
        // Arrange
        when(authLetterRepository.findById(1L)).thenReturn(Optional.of(testAuthLetter));
        when(authLetterRepository.save(any(AuthLetter.class))).thenReturn(testAuthLetter);

        // Act
        authLetterService.publish(1L);

        // Assert
        assertEquals(AuthLetterStatus.PUBLISHED, testAuthLetter.getStatus());
        verify(authLetterRepository, times(1)).save(any(AuthLetter.class));
    }

    @Test
    void testGetLookupValues() {
        // Act
        java.util.List<AuthLetterService.LookupValue> result = authLetterService.getLookupValues("AUTH_TARGET_LEVEL");

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("ORGANIZATION", result.get(0).getCode());
    }

    @Test
    void testGetOrgTree() {
        // Act
        Object result = authLetterService.getOrgTree();

        // Assert
        assertNotNull(result);
    }
}