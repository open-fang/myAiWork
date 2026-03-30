package com.auth.management.service;

import com.auth.management.dto.request.SceneMatchRequest;
import com.auth.management.dto.response.SceneMatchResponse;
import com.auth.management.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SceneMatchService Test Cases
 * Tests the rule engine matching logic
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SceneMatchServiceTest {

    @Autowired
    private SceneMatchService sceneMatchService;

    @Test
    @Order(1)
    void testMatchScene_NonExistingAuthLetter_ThrowsException() {
        // Given: Non-existing auth letter ID
        Long nonExistingId = 999999L;
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 100);

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(nonExistingId);
        request.setData(data);

        // When & Then: Should throw BusinessException
        assertThrows(BusinessException.class, () -> {
            sceneMatchService.match(request);
        }, "Should throw exception for non-existing auth letter");
    }

    @Test
    @Order(2)
    void testMatchScene_NonPublishedAuthLetter_ThrowsException() {
        // Given: Auth letter ID that is not published (test data)
        Long authLetterId = 1L;

        Map<String, Object> data = new HashMap<>();
        data.put("amount", 1000000);

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(authLetterId);
        request.setData(data);

        // When & Then: Should throw BusinessException for non-published auth letter
        assertThrows(BusinessException.class, () -> {
            sceneMatchService.match(request);
        }, "Should throw exception for non-published auth letter");
    }

    @Test
    @Order(3)
    void testMatchScene_MultipleConditions_NonPublished_ThrowsException() {
        // Given: Multiple condition data for non-published auth letter
        Long authLetterId = 1L;

        Map<String, Object> data = new HashMap<>();
        data.put("amount", 5000000);
        data.put("region", "1-1-1");
        data.put("industry", "LV1");

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(authLetterId);
        request.setData(data);

        // When & Then: Should throw BusinessException
        assertThrows(BusinessException.class, () -> {
            sceneMatchService.match(request);
        }, "Should throw exception for non-published auth letter");
    }

    @Test
    @Order(4)
    void testMatchScene_EmptyData_NonPublished_ThrowsException() {
        // Given: Empty data for non-published auth letter
        Long authLetterId = 1L;

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(authLetterId);
        request.setData(new HashMap<>());

        // When & Then: Should throw BusinessException
        assertThrows(BusinessException.class, () -> {
            sceneMatchService.match(request);
        }, "Should throw exception for non-published auth letter");
    }
}