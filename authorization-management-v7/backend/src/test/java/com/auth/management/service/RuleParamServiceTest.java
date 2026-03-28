package com.auth.management.service;

import com.auth.management.dto.request.BatchRuleParamStatusRequest;
import com.auth.management.dto.request.CreateRuleParamRequest;
import com.auth.management.dto.response.PageResponse;
import com.auth.management.dto.response.RuleParamDetailResponse;
import com.auth.management.dto.response.RuleParamOptionResponse;
import com.auth.management.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RuleParamService Test Cases
 * Tests the rule parameter configuration functionality
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RuleParamServiceTest {

    @Autowired
    private RuleParamService ruleParamService;

    @Test
    @Order(1)
    @Transactional
    void testCreateRuleParam_Success() {
        // Given: Complete request data
        CreateRuleParamRequest request = new CreateRuleParamRequest();
        request.setName("Test Amount");
        request.setNameEn("test_amount_" + System.currentTimeMillis());
        request.setBusinessObjects("[{\"businessObject\":\"Order\",\"valueLogic\":\"$.order.amount\"}]");
        request.setStatus("ACTIVE");
        request.setDataType("NUMBER");

        // When: Create rule param
        Long id = ruleParamService.create(request);

        // Then: Should return ID
        assertNotNull(id, "Created ID should not be null");
        assertTrue(id > 0, "Created ID should be positive");
    }

    @Test
    @Order(2)
    void testQueryList_Success() {
        // Given: Pagination parameters
        Integer pageNum = 1;
        Integer pageSize = 10;

        // When: Query list
        PageResponse<?> response = ruleParamService.queryList(
                null, null, null, pageNum, pageSize
        );

        // Then: Should return paginated result
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getList(), "List should not be null");
        assertTrue(response.getTotal() >= 0, "Total should be non-negative");
    }

    @Test
    @Order(3)
    void testGetOptions_ActiveOnly() {
        // When: Get options for active rule params
        List<RuleParamOptionResponse> response = ruleParamService.getOptions("ACTIVE");

        // Then: Should return options
        assertNotNull(response, "Response should not be null");
    }

    @Test
    @Order(4)
    @Transactional
    void testBatchUpdateStatus_Success() {
        // Given: Create a rule param first
        CreateRuleParamRequest request = new CreateRuleParamRequest();
        request.setName("Status Update Test");
        request.setNameEn("status_update_" + System.currentTimeMillis());
        request.setBusinessObjects("[]");
        request.setStatus("ACTIVE");
        request.setDataType("TEXT");
        Long id = ruleParamService.create(request);

        // When: Update status
        BatchRuleParamStatusRequest batchRequest = new BatchRuleParamStatusRequest();
        batchRequest.setIds(Arrays.asList(id));
        batchRequest.setStatus("INACTIVE");
        ruleParamService.batchUpdateStatus(batchRequest);

        // Then: Status should be updated
        RuleParamDetailResponse detail = ruleParamService.getDetail(id);
        assertEquals("INACTIVE", detail.getStatus(), "Status should be INACTIVE");
    }

    @Test
    @Order(5)
    @Transactional
    void testCreateRuleParam_DuplicateName() {
        // Given: Create first param
        String name = "Duplicate Test " + System.currentTimeMillis();
        CreateRuleParamRequest request1 = new CreateRuleParamRequest();
        request1.setName(name);
        request1.setNameEn("dup_test_" + System.currentTimeMillis());
        request1.setBusinessObjects("[]");
        request1.setStatus("ACTIVE");
        request1.setDataType("TEXT");
        ruleParamService.create(request1);

        // When: Try to create with same name
        CreateRuleParamRequest request2 = new CreateRuleParamRequest();
        request2.setName(name);
        request2.setNameEn("dup_test_2_" + System.currentTimeMillis());
        request2.setBusinessObjects("[]");
        request2.setStatus("ACTIVE");
        request2.setDataType("TEXT");

        // Then: Should throw exception
        assertThrows(BusinessException.class, () -> {
            ruleParamService.create(request2);
        }, "Should throw BusinessException for duplicate name");
    }

    @Test
    @Order(6)
    void testQueryList_FilterByName() {
        // Given: Create a param with specific name
        String uniqueName = "Filter Test " + System.currentTimeMillis();
        CreateRuleParamRequest request = new CreateRuleParamRequest();
        request.setName(uniqueName);
        request.setNameEn("filter_test_" + System.currentTimeMillis());
        request.setBusinessObjects("[]");
        request.setStatus("ACTIVE");
        request.setDataType("TEXT");
        ruleParamService.create(request);

        // When: Query by name
        PageResponse<?> response = ruleParamService.queryList(
                "Filter", null, null, 1, 10
        );

        // Then: Should return matching results
        assertNotNull(response.getList());
        assertTrue(response.getTotal() > 0, "Should have matching results");
    }

    @Test
    @Order(7)
    @Transactional
    void testDelete_Success() {
        // Given: Create a rule param
        CreateRuleParamRequest request = new CreateRuleParamRequest();
        request.setName("Delete Test " + System.currentTimeMillis());
        request.setNameEn("delete_test_" + System.currentTimeMillis());
        request.setBusinessObjects("[]");
        request.setStatus("ACTIVE");
        request.setDataType("TEXT");
        Long id = ruleParamService.create(request);

        // When: Delete
        ruleParamService.delete(id);

        // Then: Should throw exception when getting detail
        assertThrows(BusinessException.class, () -> {
            ruleParamService.getDetail(id);
        }, "Deleted rule param should not be found");
    }
}