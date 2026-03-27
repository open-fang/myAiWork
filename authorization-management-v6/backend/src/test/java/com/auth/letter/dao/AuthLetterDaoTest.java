package com.auth.letter.dao;

import com.auth.letter.entity.AuthLetter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 授权书 DAO 测试类
 *
 * 注意：此测试需要真实的 PostgreSQL 数据库
 * 运行方式：
 * 1. 启动 Docker 容器：docker run -d --name test-postgres -e POSTGRES_PASSWORD=test -p 5432:5432 postgres:14
 * 2. 配置 application-test.yml 中的数据库连接
 * 3. 运行测试：mvn test -Dtest=AuthLetterDaoTest -Dspring.profiles.active=test
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthLetterDaoTest {

    @Autowired
    private AuthLetterDao authLetterDao;

    private static Long testId;

    // ==================== 插入测试 ====================

    @Test
    @Order(1)
    @DisplayName("插入授权书 - 成功")
    void testInsert_Success() {
        // Given
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("测试授权书_DAO");
        authLetter.setAuthObjectLevel("[\"总部\"]");
        authLetter.setApplicableRegion("[\"华东\"]");
        authLetter.setAuthPublishLevel("[\"一级\"]");
        authLetter.setAuthPublishOrg("[\"总部\"]");
        authLetter.setPublishYear(2026);
        authLetter.setSummary("DAO测试摘要");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);

        // When
        int result = authLetterDao.insert(authLetter);

        // Then
        assertEquals(1, result);
        assertNotNull(authLetter.getId());
        testId = authLetter.getId();
    }

    // ==================== 查询测试 ====================

    @Test
    @Order(2)
    @DisplayName("根据ID查询 - 成功")
    void testSelectById_Success() {
        // Given - 先插入数据
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("查询测试");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);
        Long id = authLetter.getId();

        // When
        AuthLetter result = authLetterDao.selectById(id);

        // Then
        assertNotNull(result);
        assertEquals("查询测试", result.getName());
        assertEquals("DRAFT", result.getStatus());
    }

    @Test
    @Order(3)
    @DisplayName("根据ID查询 - 不存在")
    void testSelectById_NotFound() {
        // When
        AuthLetter result = authLetterDao.selectById(999999L);

        // Then
        assertNull(result);
    }

    @Test
    @Order(4)
    @DisplayName("查询列表 - 无条件")
    void testSelectList_NoCondition() {
        // Given - 插入测试数据
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("列表测试1");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);

        // When
        List<AuthLetter> result = authLetterDao.selectList(null, null, null, null, null, null, null, 0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    @Order(5)
    @DisplayName("查询列表 - 按名称模糊查询")
    void testSelectList_ByName() {
        // Given
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("特殊名称_测试_2026");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);

        // When
        List<AuthLetter> result = authLetterDao.selectList("特殊名称", null, null, null, null, null, null, 0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(a -> a.getName().contains("特殊名称")));
    }

    @Test
    @Order(6)
    @DisplayName("查询列表 - 按状态查询")
    void testSelectList_ByStatus() {
        // Given
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("状态测试");
        authLetter.setStatus("PUBLISHED");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);

        // When
        List<AuthLetter> result = authLetterDao.selectList(null, null, null, null, null, null, "PUBLISHED", 0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.stream().allMatch(a -> "PUBLISHED".equals(a.getStatus())));
    }

    @Test
    @Order(7)
    @DisplayName("查询列表 - 按年份查询")
    void testSelectList_ByYear() {
        // Given
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("年份测试");
        authLetter.setPublishYear(2026);
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);

        // When
        List<AuthLetter> result = authLetterDao.selectList(null, null, null, null, null, 2026, null, 0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.stream().allMatch(a -> Integer.valueOf(2026).equals(a.getPublishYear())));
    }

    @Test
    @Order(8)
    @DisplayName("统计总数 - 成功")
    void testCountList_Success() {
        // Given
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("统计测试");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);

        // When
        long count = authLetterDao.countList(null, null, null, null, null, null, null);

        // Then
        assertTrue(count > 0);
    }

    // ==================== 更新测试 ====================

    @Test
    @Order(9)
    @DisplayName("更新授权书 - 成功")
    void testUpdate_Success() {
        // Given - 先插入
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("更新前名称");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);
        Long id = authLetter.getId();

        // When - 更新
        authLetter.setName("更新后名称");
        authLetter.setSummary("更新后的摘要");
        authLetter.setUpdatedBy("updater");
        int result = authLetterDao.update(authLetter);

        // Then
        assertEquals(1, result);
        AuthLetter updated = authLetterDao.selectById(id);
        assertEquals("更新后名称", updated.getName());
        assertEquals("更新后的摘要", updated.getSummary());
        assertEquals("updater", updated.getUpdatedBy());
    }

    @Test
    @Order(10)
    @DisplayName("更新状态 - 成功")
    void testUpdateStatus_Success() {
        // Given
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("状态更新测试");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);
        Long id = authLetter.getId();

        // When
        int result = authLetterDao.updateStatus(id, "PUBLISHED", "publisher");

        // Then
        assertEquals(1, result);
        AuthLetter updated = authLetterDao.selectById(id);
        assertEquals("PUBLISHED", updated.getStatus());
    }

    // ==================== 删除测试 ====================

    @Test
    @Order(11)
    @DisplayName("逻辑删除 - 成功")
    void testDeleteById_Success() {
        // Given
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("删除测试");
        authLetter.setStatus("DRAFT");
        authLetter.setCreatedBy("test_user");
        authLetter.setDeleteFlag(0);
        authLetterDao.insert(authLetter);
        Long id = authLetter.getId();

        // When
        int result = authLetterDao.deleteById(id);

        // Then
        assertEquals(1, result);
        // 验证已被逻辑删除（selectById 应该返回 null 或 deleteFlag=1）
        AuthLetter deleted = authLetterDao.selectById(id);
        // 根据实际实现，可能是 null 或者 deleteFlag=1
        assertTrue(deleted == null || deleted.getDeleteFlag() == 1);
    }

    @Test
    @Order(12)
    @DisplayName("批量删除 - 成功")
    void testDeleteByIds_Success() {
        // Given - 插入多条数据
        AuthLetter letter1 = new AuthLetter();
        letter1.setName("批量删除1");
        letter1.setStatus("DRAFT");
        letter1.setCreatedBy("test_user");
        letter1.setDeleteFlag(0);
        authLetterDao.insert(letter1);

        AuthLetter letter2 = new AuthLetter();
        letter2.setName("批量删除2");
        letter2.setStatus("DRAFT");
        letter2.setCreatedBy("test_user");
        letter2.setDeleteFlag(0);
        authLetterDao.insert(letter2);

        List<Long> ids = Arrays.asList(letter1.getId(), letter2.getId());

        // When
        int result = authLetterDao.deleteByIds(ids);

        // Then
        assertEquals(2, result);
    }

    @Test
    @Order(13)
    @DisplayName("批量更新状态 - 成功")
    void testBatchUpdateStatus_Success() {
        // Given
        AuthLetter letter1 = new AuthLetter();
        letter1.setName("批量状态1");
        letter1.setStatus("DRAFT");
        letter1.setCreatedBy("test_user");
        letter1.setDeleteFlag(0);
        authLetterDao.insert(letter1);

        AuthLetter letter2 = new AuthLetter();
        letter2.setName("批量状态2");
        letter2.setStatus("DRAFT");
        letter2.setCreatedBy("test_user");
        letter2.setDeleteFlag(0);
        authLetterDao.insert(letter2);

        List<Long> ids = Arrays.asList(letter1.getId(), letter2.getId());

        // When
        int result = authLetterDao.batchUpdateStatus(ids, "PUBLISHED", "batch_user");

        // Then
        assertEquals(2, result);
    }
}