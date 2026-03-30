package com.auth.management.service;

import com.auth.management.dto.request.BatchAuthLetterRequest;
import com.auth.management.dto.request.CreateAuthLetterRequest;
import com.auth.management.dto.response.AuthLetterDetailResponse;
import com.auth.management.dto.response.BatchOperationResponse;
import com.auth.management.dto.response.PageResponse;
import com.auth.management.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthLetterService Test Cases
 * Tests the core business logic for authorization letter management
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthLetterServiceTest {

    @Autowired
    private AuthLetterService authLetterService;

    private static Long testAuthLetterId;

    @Test
    @Order(1)
    @Transactional
    void testCreateAuthLetter_Success() {
        // Given: Complete request data
        CreateAuthLetterRequest request = new CreateAuthLetterRequest();
        request.setName("Test Authorization Letter " + System.currentTimeMillis());
        request.setAuthObjectLevel(Arrays.asList("LEVEL1", "LEVEL2"));
        request.setApplicableRegion(Arrays.asList("1", "1-1"));
        request.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        request.setAuthPublishOrg(Arrays.asList("1"));
        request.setPublishYear(2024);
        request.setSummary("Test summary for authorization letter");

        // When: Create authorization letter
        Long id = authLetterService.create(request);

        // Then: Should return ID
        assertNotNull(id, "Created ID should not be null");
        assertTrue(id > 0, "Created ID should be positive");

        testAuthLetterId = id;
    }

    @Test
    @Order(2)
    void testQueryList_WithPagination() {
        // Given: Pagination parameters
        Integer pageNum = 1;
        Integer pageSize = 10;

        // When: Query list
        PageResponse<?> response = authLetterService.queryList(
                null, null, null, null, null, null, null,
                pageNum, pageSize
        );

        // Then: Should return paginated result
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getList(), "List should not be null");
        assertTrue(response.getTotal() >= 0, "Total should be non-negative");
        assertEquals(pageNum, response.getPageNum(), "Page number should match");
        assertEquals(pageSize, response.getPageSize(), "Page size should match");
    }

    @Test
    @Order(3)
    void testGetDetail_ExistingId() {
        // Given: An existing authorization letter
        // First create one
        CreateAuthLetterRequest request = new CreateAuthLetterRequest();
        request.setName("Detail Test Letter " + System.currentTimeMillis());
        request.setAuthObjectLevel(Arrays.asList("LEVEL1"));
        request.setApplicableRegion(Arrays.asList("1"));
        request.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        request.setAuthPublishOrg(Arrays.asList("1"));
        request.setPublishYear(2024);
        request.setSummary("Test summary");
        Long id = authLetterService.create(request);

        // When: Get detail
        AuthLetterDetailResponse response = authLetterService.getDetail(id);

        // Then: Should return detail
        assertNotNull(response, "Response should not be null");
        assertEquals(id, response.getId(), "ID should match");
        assertNotNull(response.getName(), "Name should not be null");
        assertEquals("DRAFT", response.getStatus(), "Default status should be DRAFT");
    }

    @Test
    @Order(4)
    void testGetDetail_NonExistingId() {
        // Given: Non-existing ID
        Long nonExistingId = 999999L;

        // When & Then: Should throw exception
        assertThrows(BusinessException.class, () -> {
            authLetterService.getDetail(nonExistingId);
        }, "Should throw BusinessException for non-existing ID");
    }

    @Test
    @Order(5)
    @Transactional
    void testCreateAuthLetter_DuplicateName() {
        // Given: Create first letter
        String name = "Duplicate Test " + System.currentTimeMillis();
        CreateAuthLetterRequest request1 = new CreateAuthLetterRequest();
        request1.setName(name);
        request1.setAuthObjectLevel(Arrays.asList("LEVEL1"));
        request1.setApplicableRegion(Arrays.asList("1"));
        request1.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        request1.setAuthPublishOrg(Arrays.asList("1"));
        request1.setPublishYear(2024);
        request1.setSummary("First letter");
        authLetterService.create(request1);

        // When: Try to create with same name
        CreateAuthLetterRequest request2 = new CreateAuthLetterRequest();
        request2.setName(name);
        request2.setAuthObjectLevel(Arrays.asList("LEVEL1"));
        request2.setApplicableRegion(Arrays.asList("1"));
        request2.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        request2.setAuthPublishOrg(Arrays.asList("1"));
        request2.setPublishYear(2024);
        request2.setSummary("Second letter");

        // Then: Should throw exception
        assertThrows(BusinessException.class, () -> {
            authLetterService.create(request2);
        }, "Should throw BusinessException for duplicate name");
    }

    @Test
    @Order(6)
    @Transactional
    void testUpdate_DraftStatus() {
        // Given: Create a draft letter
        CreateAuthLetterRequest createRequest = new CreateAuthLetterRequest();
        createRequest.setName("Update Test " + System.currentTimeMillis());
        createRequest.setAuthObjectLevel(Arrays.asList("LEVEL1"));
        createRequest.setApplicableRegion(Arrays.asList("1"));
        createRequest.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        createRequest.setAuthPublishOrg(Arrays.asList("1"));
        createRequest.setPublishYear(2024);
        createRequest.setSummary("Original summary");
        Long id = authLetterService.create(createRequest);

        // When: Update the letter
        CreateAuthLetterRequest updateRequest = new CreateAuthLetterRequest();
        updateRequest.setName("Updated Name " + System.currentTimeMillis());
        updateRequest.setAuthObjectLevel(Arrays.asList("LEVEL1", "LEVEL2"));
        updateRequest.setApplicableRegion(Arrays.asList("1", "1-1"));
        updateRequest.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        updateRequest.setAuthPublishOrg(Arrays.asList("1"));
        updateRequest.setPublishYear(2025);
        updateRequest.setSummary("Updated summary");

        authLetterService.update(id, updateRequest);

        // Then: Should be updated
        AuthLetterDetailResponse response = authLetterService.getDetail(id);
        assertEquals("Updated summary", response.getSummary());
        assertEquals(2025, response.getPublishYear());
    }

    @Test
    @Order(7)
    @Transactional
    void testDelete_Success() {
        // Given: Create a letter
        CreateAuthLetterRequest request = new CreateAuthLetterRequest();
        request.setName("Delete Test " + System.currentTimeMillis());
        request.setAuthObjectLevel(Arrays.asList("LEVEL1"));
        request.setApplicableRegion(Arrays.asList("1"));
        request.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        request.setAuthPublishOrg(Arrays.asList("1"));
        request.setPublishYear(2024);
        request.setSummary("To be deleted");
        Long id = authLetterService.create(request);

        // When: Delete
        authLetterService.delete(id);

        // Then: Should not be found (logically deleted)
        assertThrows(BusinessException.class, () -> {
            authLetterService.getDetail(id);
        }, "Deleted letter should not be found");
    }

    @Test
    @Order(8)
    void testBatchOperation_Delete() {
        // Given: Create multiple letters
        Long[] ids = new Long[3];
        for (int i = 0; i < 3; i++) {
            CreateAuthLetterRequest request = new CreateAuthLetterRequest();
            request.setName("Batch Test " + System.currentTimeMillis() + "_" + i);
            request.setAuthObjectLevel(Arrays.asList("LEVEL1"));
            request.setApplicableRegion(Arrays.asList("1"));
            request.setAuthPublishLevel(Arrays.asList("LEVEL1"));
            request.setAuthPublishOrg(Arrays.asList("1"));
            request.setPublishYear(2024);
            request.setSummary("Batch test");
            ids[i] = authLetterService.create(request);
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }

        // When: Batch delete
        BatchAuthLetterRequest batchRequest = new BatchAuthLetterRequest();
        batchRequest.setIds(Arrays.asList(ids));
        batchRequest.setOperation("DELETE");

        BatchOperationResponse response = authLetterService.batchOperation(batchRequest);

        // Then: All should succeed
        assertEquals(3, response.getSuccessCount(), "All 3 should succeed");
        assertEquals(0, response.getFailCount(), "None should fail");
    }

    @Test
    @Order(9)
    void testQueryList_FilterByStatus() {
        // Given: Create letters with different status
        CreateAuthLetterRequest request = new CreateAuthLetterRequest();
        request.setName("Status Filter Test " + System.currentTimeMillis());
        request.setAuthObjectLevel(Arrays.asList("LEVEL1"));
        request.setApplicableRegion(Arrays.asList("1"));
        request.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        request.setAuthPublishOrg(Arrays.asList("1"));
        request.setPublishYear(2024);
        request.setSummary("Status filter test");
        authLetterService.create(request);

        // When: Query by DRAFT status
        PageResponse<?> response = authLetterService.queryList(
                null, null, null, null, null, null, "DRAFT",
                1, 10
        );

        // Then: Should only return DRAFT items
        assertNotNull(response.getList());
        assertTrue(response.getTotal() > 0, "Should have DRAFT items");
    }

    @Test
    @Order(10)
    void testQueryList_FilterByName() {
        // Given: Create letter with specific name
        String uniqueName = "Name Filter Test " + System.currentTimeMillis();
        CreateAuthLetterRequest request = new CreateAuthLetterRequest();
        request.setName(uniqueName);
        request.setAuthObjectLevel(Arrays.asList("LEVEL1"));
        request.setApplicableRegion(Arrays.asList("1"));
        request.setAuthPublishLevel(Arrays.asList("LEVEL1"));
        request.setAuthPublishOrg(Arrays.asList("1"));
        request.setPublishYear(2024);
        request.setSummary("Name filter test");
        authLetterService.create(request);

        // When: Query by name
        PageResponse<?> response = authLetterService.queryList(
                "Name Filter", null, null, null, null, null, null,
                1, 10
        );

        // Then: Should return matching items
        assertNotNull(response.getList());
        assertTrue(response.getTotal() > 0, "Should have matching items");
    }
}