package com.auth.management.mapper;

import com.auth.management.entity.AuthLetter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthLetterMapper Test Cases
 * Tests the database operations for authorization letter
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthLetterMapperTest {

    @Autowired
    private AuthLetterMapper authLetterMapper;

    @Test
    @Order(1)
    void testSelectListWithConditions_Success() {
        // Given: Query parameters
        Map<String, Object> params = new HashMap<>();

        // When: Query with conditions
        List<AuthLetter> result = authLetterMapper.selectListWithConditions(params);

        // Then: Should return result
        assertNotNull(result, "Result should not be null");
    }

    @Test
    @Order(2)
    void testSelectListWithConditions_FilterByStatus() {
        // Given: Filter by status
        Map<String, Object> params = new HashMap<>();
        params.put("status", "DRAFT");

        // When: Query with filter
        List<AuthLetter> result = authLetterMapper.selectListWithConditions(params);

        // Then: Should return filtered result
        assertNotNull(result, "Result should not be null");
        for (AuthLetter letter : result) {
            assertEquals("DRAFT", letter.getStatus(), "All results should be DRAFT status");
        }
    }

    @Test
    @Order(3)
    void testSelectListWithConditions_FilterByName() {
        // Given: Filter by name
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Test");

        // When: Query with name filter
        List<AuthLetter> result = authLetterMapper.selectListWithConditions(params);

        // Then: Should return matching results
        assertNotNull(result, "Result should not be null");
    }

    @Test
    @Order(4)
    void testCountListWithConditions_Success() {
        // Given: Query parameters
        Map<String, Object> params = new HashMap<>();

        // When: Count with conditions
        Long count = authLetterMapper.countListWithConditions(params);

        // Then: Should return count
        assertNotNull(count, "Count should not be null");
        assertTrue(count >= 0, "Count should be non-negative");
    }

    @Test
    @Order(5)
    @Transactional
    void testInsert_Success() {
        // Given: New authorization letter
        AuthLetter letter = new AuthLetter();
        letter.setName("Mapper Test " + System.currentTimeMillis());
        letter.setAuthObjectLevel("[\"LEVEL1\"]");
        letter.setApplicableRegion("[\"1\"]");
        letter.setAuthPublishLevel("[\"LEVEL1\"]");
        letter.setAuthPublishOrg("[\"1\"]");
        letter.setPublishYear(2024);
        letter.setSummary("Mapper insert test");
        letter.setStatus("DRAFT");

        // When: Insert
        int result = authLetterMapper.insert(letter);

        // Then: Should succeed
        assertEquals(1, result, "Insert should affect 1 row");
        assertNotNull(letter.getId(), "ID should be generated");
    }

    @Test
    @Order(6)
    @Transactional
    void testUpdateById_Success() {
        // Given: First insert a record
        AuthLetter letter = new AuthLetter();
        letter.setName("Update Test " + System.currentTimeMillis());
        letter.setAuthObjectLevel("[\"LEVEL1\"]");
        letter.setApplicableRegion("[\"1\"]");
        letter.setAuthPublishLevel("[\"LEVEL1\"]");
        letter.setAuthPublishOrg("[\"1\"]");
        letter.setPublishYear(2024);
        letter.setSummary("Original");
        letter.setStatus("DRAFT");
        authLetterMapper.insert(letter);

        // When: Update
        letter.setSummary("Updated");
        int result = authLetterMapper.updateById(letter);

        // Then: Should succeed
        assertEquals(1, result, "Update should affect 1 row");
    }

    @Test
    @Order(7)
    @Transactional
    void testDeleteById_LogicalDelete() {
        // Given: Insert a record
        AuthLetter letter = new AuthLetter();
        letter.setName("Delete Test " + System.currentTimeMillis());
        letter.setAuthObjectLevel("[\"LEVEL1\"]");
        letter.setApplicableRegion("[\"1\"]");
        letter.setAuthPublishLevel("[\"LEVEL1\"]");
        letter.setAuthPublishOrg("[\"1\"]");
        letter.setPublishYear(2024);
        letter.setSummary("To be deleted");
        letter.setStatus("DRAFT");
        authLetterMapper.insert(letter);
        Long id = letter.getId();

        // When: Logical delete
        int result = authLetterMapper.deleteById(id);

        // Then: Should succeed
        assertEquals(1, result, "Delete should affect 1 row");
    }

    @Test
    @Order(8)
    void testSelectById_Existing() {
        // Given: Insert a record
        AuthLetter letter = new AuthLetter();
        letter.setName("Select Test " + System.currentTimeMillis());
        letter.setAuthObjectLevel("[\"LEVEL1\"]");
        letter.setApplicableRegion("[\"1\"]");
        letter.setAuthPublishLevel("[\"LEVEL1\"]");
        letter.setAuthPublishOrg("[\"1\"]");
        letter.setPublishYear(2024);
        letter.setSummary("Select test");
        letter.setStatus("DRAFT");
        authLetterMapper.insert(letter);
        Long id = letter.getId();

        // When: Select by ID
        AuthLetter result = authLetterMapper.selectById(id);

        // Then: Should return the record
        assertNotNull(result, "Should find the record");
        assertEquals(id, result.getId(), "ID should match");
    }

    @Test
    @Order(9)
    void testCheckNameExists_Unique() {
        // Given: Unique name
        String uniqueName = "Unique Name Test " + System.currentTimeMillis();

        // When: Check if name exists
        Integer count = authLetterMapper.checkNameExists(uniqueName, null);

        // Then: Should be 0 or null
        assertTrue(count == null || count == 0, "Unique name should not exist");
    }
}