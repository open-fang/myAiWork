package com.auth.letter.service;

import com.auth.letter.dto.*;
import com.auth.letter.entity.*;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthLetterService Test
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthLetterServiceTest {

    @Autowired
    private AuthLetterService authLetterService;

    @Autowired
    private AuthLetterRepository authLetterRepository;

    @Autowired
    private AuthLookupRepository lookupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthLookup createLookup(String type, String code, String name, String parentCode) {
        AuthLookup lookup = new AuthLookup();
        lookup.setLookupType(type);
        lookup.setLookupCode(code);
        lookup.setLookupName(name);
        lookup.setParentCode(parentCode);
        lookup.setStatus("ACTIVE");
        return lookupRepository.save(lookup);
    }

    @BeforeEach
    void setUp() {
        // Clear existing data
        authLetterRepository.deleteAll();
        lookupRepository.deleteAll();

        // Create test lookup data
        createLookup("AUTH_TARGET_LEVEL", "LEVEL_1", "集团", null);
        createLookup("AUTH_TARGET_LEVEL", "LEVEL_2", "区域", null);
        createLookup("AUTH_TARGET_LEVEL", "LEVEL_3", "代表处", null);

        createLookup("AUTH_PUBLISH_LEVEL", "PUBLISH_LEVEL_1", "一级发布", null);
        createLookup("AUTH_PUBLISH_LEVEL", "PUBLISH_LEVEL_2", "二级发布", null);

        createLookup("DECISION_LEVEL", "DEC_LEVEL_1", "一级决策", null);
        createLookup("DECISION_LEVEL", "DEC_LEVEL_2", "二级决策", null);

        createLookup("ORG_TREE", "ORG_HEADQUARTERS", "机关", null);
        createLookup("ORG_TREE", "ORG_REGION_1", "地区部A", "ORG_HEADQUARTERS");

        createLookup("DOC_TYPE", "DOC_PDF", "PDF文档", null);
        createLookup("DOC_TYPE", "DOC_WORD", "Word文档", null);
    }

    @Test
    void testCreateAuthLetter() {
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("测试授权书");
        dto.setAuthTargetLevel(Arrays.asList("LEVEL_1", "LEVEL_2"));
        dto.setAuthPublishLevel(Arrays.asList("PUBLISH_LEVEL_1"));
        dto.setPublishYear(2026);
        dto.setContentSummary("这是一个测试授权书");

        Long id = authLetterService.create(dto);

        assertNotNull(id);
        assertTrue(id > 0);

        AuthLetterDetailVO detail = authLetterService.getDetail(id);
        assertNotNull(detail);
        assertEquals("测试授权书", detail.getName());
        assertEquals(AuthLetterStatus.DRAFT.getCode(), detail.getStatus());
        assertEquals(2, detail.getAuthTargetLevel().size());
    }

    @Test
    void testUpdateAuthLetter() {
        // Create first
        AuthLetterDetailDTO createDto = new AuthLetterDetailDTO();
        createDto.setName("原始授权书");
        createDto.setPublishYear(2025);

        Long id = authLetterService.create(createDto);

        // Update
        AuthLetterDetailDTO updateDto = new AuthLetterDetailDTO();
        updateDto.setName("更新后的授权书");
        updateDto.setAuthTargetLevel(Arrays.asList("LEVEL_3"));
        updateDto.setPublishYear(2026);
        updateDto.setContentSummary("更新后的内容摘要");

        authLetterService.update(id, updateDto);

        AuthLetterDetailVO detail = authLetterService.getDetail(id);
        assertEquals("更新后的授权书", detail.getName());
        assertEquals(2026, detail.getPublishYear());
        assertEquals("更新后的内容摘要", detail.getContentSummary());
    }

    @Test
    void testPublishAuthLetter() {
        // Create
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("待发布授权书");

        Long id = authLetterService.create(dto);

        // Publish
        authLetterService.publish(id);

        AuthLetterDetailVO detail = authLetterService.getDetail(id);
        assertEquals(AuthLetterStatus.PUBLISHED.getCode(), detail.getStatus());
        assertNotNull(detail.getPublishedAt());
    }

    @Test
    void testExpireAuthLetter() {
        // Create and publish
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("待失效授权书");

        Long id = authLetterService.create(dto);
        authLetterService.publish(id);

        // Expire
        authLetterService.expire(id);

        AuthLetterDetailVO detail = authLetterService.getDetail(id);
        assertEquals(AuthLetterStatus.EXPIRED.getCode(), detail.getStatus());
    }

    @Test
    void testDeleteAuthLetter() {
        // Create
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("待删除授权书");

        Long id = authLetterService.create(dto);

        // Delete
        authLetterService.delete(id);

        // Verify deleted
        assertThrows(RuntimeException.class, () -> {
            authLetterService.getDetail(id);
        });
    }

    @Test
    void testQueryList() {
        // Create multiple auth letters
        for (int i = 1; i <= 3; i++) {
            AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
            dto.setName("授权书" + i);
            dto.setPublishYear(2026);
            authLetterService.create(dto);
        }

        // Query
        AuthLetterQueryDTO queryDTO = new AuthLetterQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        PageResult<AuthLetterListVO> result = authLetterService.queryList(queryDTO);

        assertNotNull(result);
        assertEquals(3, result.getTotal());
        assertEquals(3, result.getList().size());
    }

    @Test
    void testQueryListWithFilter() {
        // Create auth letters with different years
        AuthLetterDetailDTO dto1 = new AuthLetterDetailDTO();
        dto1.setName("2025授权书");
        dto1.setPublishYear(2025);
        authLetterService.create(dto1);

        AuthLetterDetailDTO dto2 = new AuthLetterDetailDTO();
        dto2.setName("2026授权书");
        dto2.setPublishYear(2026);
        authLetterService.create(dto2);

        // Query with year filter
        AuthLetterQueryDTO queryDTO = new AuthLetterQueryDTO();
        queryDTO.setPublishYear(2026);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        PageResult<AuthLetterListVO> result = authLetterService.queryList(queryDTO);

        assertEquals(1, result.getTotal());
        assertEquals("2026授权书", result.getList().get(0).getName());
    }

    @Test
    void testBatchPublish() {
        // Create multiple drafts
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
            dto.setName("批量发布测试" + i);
            ids.add(authLetterService.create(dto));
        }

        // Batch publish
        int count = authLetterService.batchPublish(ids);

        assertEquals(3, count);

        // Verify all are published
        for (Long id : ids) {
            AuthLetterDetailVO detail = authLetterService.getDetail(id);
            assertEquals(AuthLetterStatus.PUBLISHED.getCode(), detail.getStatus());
        }
    }

    @Test
    void testBatchExpire() {
        // Create and publish multiple
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
            dto.setName("批量失效测试" + i);
            Long id = authLetterService.create(dto);
            authLetterService.publish(id);
            ids.add(id);
        }

        // Batch expire
        int count = authLetterService.batchExpire(ids);

        assertEquals(2, count);
    }

    @Test
    void testBatchDelete() {
        // Create multiple
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
            dto.setName("批量删除测试" + i);
            ids.add(authLetterService.create(dto));
        }

        // Batch delete
        int count = authLetterService.batchDelete(ids);

        assertEquals(2, count);
    }

    @Test
    void testGetOperationLogs() {
        // Create
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("日志测试授权书");
        Long id = authLetterService.create(dto);

        // Publish
        authLetterService.publish(id);

        // Get logs
        PageResult<OperationLogVO> logs = authLetterService.getOperationLogs(id, 1, 10);

        assertNotNull(logs);
        assertTrue(logs.getTotal() >= 2);
    }

    @Test
    void testCreateWithAttachmentsAndScenes() {
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("完整授权书");

        // Add attachment
        AttachmentDTO attachment = new AttachmentDTO();
        attachment.setDocId("DOC001");
        attachment.setDocName("测试文档.pdf");
        attachment.setDocType("DOC_PDF");
        dto.setAttachments(Arrays.asList(attachment));

        // Add scene
        SceneDTO scene = new SceneDTO();
        scene.setName("测试场景");
        scene.setIndustry(Arrays.asList("IND_TECH"));
        scene.setBusinessScenario("BIZ_SALE");
        scene.setDecisionLevel("DEC_LEVEL_1");
        scene.setRuleDetail("测试规则详情");
        dto.setScenes(Arrays.asList(scene));

        Long id = authLetterService.create(dto);

        AuthLetterDetailVO detail = authLetterService.getDetail(id);
        assertNotNull(detail);

        // Verify attachments
        assertNotNull(detail.getAttachments());
        assertEquals(1, detail.getAttachments().size());
        assertEquals("测试文档.pdf", detail.getAttachments().get(0).getDocName());

        // Verify scenes
        assertNotNull(detail.getScenes());
        assertEquals(1, detail.getScenes().size());
        assertEquals("测试场景", detail.getScenes().get(0).getName());
    }

    @Test
    void testCannotUpdatePublishedLetter() {
        // Create and publish
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("已发布授权书");
        Long id = authLetterService.create(dto);
        authLetterService.publish(id);

        // Try to update
        AuthLetterDetailDTO updateDto = new AuthLetterDetailDTO();
        updateDto.setName("尝试更新");

        assertThrows(RuntimeException.class, () -> {
            authLetterService.update(id, updateDto);
        });
    }

    @Test
    void testCannotPublishNonDraftLetter() {
        // Create and publish
        AuthLetterDetailDTO dto = new AuthLetterDetailDTO();
        dto.setName("已发布授权书");
        Long id = authLetterService.create(dto);
        authLetterService.publish(id);

        // Try to publish again
        assertThrows(RuntimeException.class, () -> {
            authLetterService.publish(id);
        });
    }
}