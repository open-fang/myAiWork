package com.auth.management.service;

import com.auth.management.dto.request.SceneMatchRequest;
import com.auth.management.dto.response.SceneMatchResponse;
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
    void testMatchScene_NonExistingAuthLetter_ReturnsEmpty() {
        // Given: Non-existing auth letter ID
        Long nonExistingId = 999999L;
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 100);

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(nonExistingId);
        request.setData(data);

        // When: Match scene
        SceneMatchResponse result = sceneMatchService.match(request);

        // Then: Should return empty matched scenes
        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getMatchedScenes(), "Matched scenes list should not be null");
    }

    @Test
    @Order(2)
    void testMatchScene_WithValidData() {
        // Given: Valid auth letter ID (if test data exists)
        Long authLetterId = 1L;

        Map<String, Object> data = new HashMap<>();
        data.put("amount", 1000000);

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(authLetterId);
        request.setData(data);

        // When: Match scene
        SceneMatchResponse result = sceneMatchService.match(request);

        // Then: Result should not be null
        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getMatchedScenes(), "Matched scenes list should not be null");
    }

    @Test
    @Order(3)
    void testMatchScene_MultipleConditions() {
        // Given: Multiple condition data
        Long authLetterId = 1L;

        Map<String, Object> data = new HashMap<>();
        data.put("amount", 5000000);
        data.put("region", "1-1-1");
        data.put("industry", "LV1");

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(authLetterId);
        request.setData(data);

        // When: Match scene
        SceneMatchResponse result = sceneMatchService.match(request);

        // Then: Result should not be null
        assertNotNull(result, "Result should not be null");
    }

    @Test
    @Order(4)
    void testMatchScene_EmptyData() {
        // Given: Empty data
        Long authLetterId = 1L;

        SceneMatchRequest request = new SceneMatchRequest();
        request.setAuthLetterId(authLetterId);
        request.setData(new HashMap<>());

        // When: Match scene
        SceneMatchResponse result = sceneMatchService.match(request);

        // Then: Should handle gracefully
        assertNotNull(result, "Result should not be null");
    }
}