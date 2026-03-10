package com.auth.letter;

import com.auth.letter.dto.AuthLetterDTO;
import com.auth.letter.dto.MatchRequest;
import com.auth.letter.dto.MatchResult;
import com.auth.letter.dto.SceneDTO;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.service.AuthLetterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthLetterServiceTest {

    @Autowired
    private AuthLetterService authLetterService;

    @Autowired
    private AuthLetterRepository authLetterRepository;

    private AuthLetterDTO testAuthLetter;

    @BeforeEach
    void setUp() {
        testAuthLetter = new AuthLetterDTO();
        testAuthLetter.setName("测试授权书");
        testAuthLetter.setDescription("这是一个测试授权书");
        testAuthLetter.setPublishLevel(OrgLevel.ORGANIZATION);
        testAuthLetter.setAuthorizedLevel(OrgLevel.REPRESENTATIVE_OFFICE);
        testAuthLetter.setValidFrom(LocalDateTime.now());
        testAuthLetter.setValidTo(LocalDateTime.now().plusDays(30));
    }

    @Test
    @DisplayName("创建授权书 - 成功")
    void testCreateAuthLetter() {
        AuthLetterDTO result = authLetterService.create(testAuthLetter);

        assertNotNull(result.getId());
        assertEquals("测试授权书", result.getName());
        assertEquals(AuthLetterStatus.DRAFT, result.getStatus());
        assertEquals(OrgLevel.ORGANIZATION, result.getPublishLevel());
        assertEquals(OrgLevel.REPRESENTATIVE_OFFICE, result.getAuthorizedLevel());
    }

    @Test
    @DisplayName("获取授权书 - 成功")
    void testGetAuthLetter() {
        AuthLetterDTO created = authLetterService.create(testAuthLetter);
        AuthLetterDTO result = authLetterService.getById(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("测试授权书", result.getName());
    }

    @Test
    @DisplayName("更新授权书 - 成功")
    void testUpdateAuthLetter() {
        AuthLetterDTO created = authLetterService.create(testAuthLetter);

        AuthLetterDTO updateDto = new AuthLetterDTO();
        updateDto.setName("更新后的授权书");
        updateDto.setDescription("更新后的描述");
        updateDto.setPublishLevel(OrgLevel.REGIONAL_DEPT);
        updateDto.setAuthorizedLevel(OrgLevel.OFFICE);

        AuthLetterDTO result = authLetterService.update(created.getId(), updateDto);

        assertEquals("更新后的授权书", result.getName());
        assertEquals("更新后的描述", result.getDescription());
        assertEquals(OrgLevel.REGIONAL_DEPT, result.getPublishLevel());
        assertEquals(OrgLevel.OFFICE, result.getAuthorizedLevel());
    }

    @Test
    @DisplayName("发布授权书 - 成功")
    void testPublishAuthLetter() {
        AuthLetterDTO created = authLetterService.create(testAuthLetter);
        AuthLetterDTO result = authLetterService.publish(created.getId());

        assertEquals(AuthLetterStatus.PUBLISHED, result.getStatus());
        assertNotNull(result.getPublishedAt());
    }

    @Test
    @DisplayName("删除授权书 - 成功")
    void testDeleteAuthLetter() {
        AuthLetterDTO created = authLetterService.create(testAuthLetter);
        authLetterService.delete(created.getId());

        assertFalse(authLetterRepository.findById(created.getId()).isPresent());
    }

    @Test
    @DisplayName("删除已发布的授权书 - 失败")
    void testDeletePublishedAuthLetter() {
        AuthLetterDTO created = authLetterService.create(testAuthLetter);
        authLetterService.publish(created.getId());

        assertThrows(Exception.class, () -> authLetterService.delete(created.getId()));
    }

    @Test
    @DisplayName("更新已发布的授权书 - 失败")
    void testUpdatePublishedAuthLetter() {
        AuthLetterDTO created = authLetterService.create(testAuthLetter);
        authLetterService.publish(created.getId());

        assertThrows(Exception.class, () -> authLetterService.update(created.getId(), testAuthLetter));
    }

    @Test
    @DisplayName("获取所有授权书")
    void testGetAllAuthLetters() {
        authLetterService.create(testAuthLetter);

        AuthLetterDTO another = new AuthLetterDTO();
        another.setName("另一个授权书");
        another.setDescription("另一个测试授权书");
        authLetterService.create(another);

        List<AuthLetterDTO> results = authLetterService.getAll();

        assertTrue(results.size() >= 2);
    }
}