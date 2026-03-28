package com.auth.management.service;

import com.auth.management.dto.request.AnswerDTO;
import com.auth.management.dto.request.AnswerTextDTO;
import com.auth.management.dto.request.CreateQuestionRequest;
import com.auth.management.dto.request.QuestionTextDTO;
import com.auth.management.dto.response.CreateQuestionResponse;
import com.auth.management.dto.response.PageResponse;
import com.auth.management.dto.response.QuestionDetailResponse;
import com.auth.management.dto.response.QuestionOptionResponse;
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
 * QuestionnaireService Test Cases
 * Tests the questionnaire question and answer management
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuestionnaireServiceTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Test
    @Order(1)
    @Transactional
    void testCreateQuestion_Success() {
        // Given: Complete question request
        CreateQuestionRequest request = new CreateQuestionRequest();
        request.setQuestionTexts(Arrays.asList(
                createQuestionText("Please select authorization type", "ZH"),
                createQuestionText("Please select authorization type (EN)", "EN")
        ));
        request.setAnswers(Arrays.asList(
                createAnswer(1, "Full authorization", "Partial authorization"),
                createAnswer(2, "Full auth (EN)", "Partial auth (EN)")
        ));

        // When: Create question
        CreateQuestionResponse response = questionnaireService.create(request);

        // Then: Should return response with ID and code
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getId(), "Created ID should not be null");
        assertNotNull(response.getQuestionCode(), "Question code should be generated");
    }

    @Test
    @Order(2)
    void testQueryList_Success() {
        // Given: Pagination parameters
        Integer pageNum = 1;
        Integer pageSize = 10;

        // When: Query list
        PageResponse<?> response = questionnaireService.queryList(
                null, null, null, null, null, null, null, null, null,
                pageNum, pageSize
        );

        // Then: Should return paginated result
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getList(), "List should not be null");
        assertTrue(response.getTotal() >= 0, "Total should be non-negative");
    }

    @Test
    @Order(3)
    void testGetOptions_Success() {
        // When: Get question options
        List<QuestionOptionResponse> options = questionnaireService.getOptions("ZH");

        // Then: Should return options
        assertNotNull(options, "Options should not be null");
    }

    @Test
    @Order(4)
    @Transactional
    void testDelete_Success() {
        // Given: Create a question first
        CreateQuestionRequest request = new CreateQuestionRequest();
        request.setQuestionTexts(Arrays.asList(
                createQuestionText("Delete test question", "ZH")
        ));
        request.setAnswers(Arrays.asList(
                createAnswer(1, "Option A", "Option B")
        ));
        CreateQuestionResponse response = questionnaireService.create(request);
        Long id = response.getId();

        // When: Delete
        questionnaireService.delete(id);

        // Then: Should not be found
        assertThrows(BusinessException.class, () -> {
            questionnaireService.getDetail(id);
        }, "Deleted question should not be found");
    }

    @Test
    @Order(5)
    @Transactional
    void testUpdate_Success() {
        // Given: Create a question first
        CreateQuestionRequest createRequest = new CreateQuestionRequest();
        createRequest.setQuestionTexts(Arrays.asList(
                createQuestionText("Original question", "ZH")
        ));
        createRequest.setAnswers(Arrays.asList(
                createAnswer(1, "Original A", "Original B")
        ));
        CreateQuestionResponse response = questionnaireService.create(createRequest);
        Long id = response.getId();

        // When: Update
        CreateQuestionRequest updateRequest = new CreateQuestionRequest();
        updateRequest.setQuestionTexts(Arrays.asList(
                createQuestionText("Updated question", "ZH")
        ));
        updateRequest.setAnswers(Arrays.asList(
                createAnswer(1, "Updated A", "Updated B")
        ));
        questionnaireService.update(id, updateRequest);

        // Then: Should be updated
        QuestionDetailResponse detail = questionnaireService.getDetail(id);
        assertNotNull(detail, "Detail should not be null");
    }

    @Test
    @Order(6)
    void testQueryList_FilterByLanguage() {
        // Given: Filter by language
        String language = "ZH";

        // When: Query with language filter
        PageResponse<?> response = questionnaireService.queryList(
                null, null, language, null, null, null, null, null, null,
                1, 10
        );

        // Then: Should return filtered result
        assertNotNull(response, "Response should not be null");
    }

    // Helper methods
    private QuestionTextDTO createQuestionText(String text, String language) {
        QuestionTextDTO dto = new QuestionTextDTO();
        dto.setQuestionText(text);
        dto.setLanguage(language);
        return dto;
    }

    private AnswerDTO createAnswer(Integer sortOrder, String... texts) {
        AnswerDTO dto = new AnswerDTO();
        dto.setSortOrder(sortOrder);
        dto.setAnswerTexts(Arrays.asList(
                createAnswerText(texts[0], "ZH"),
                texts.length > 1 ? createAnswerText(texts[1], "EN") : createAnswerText(texts[0], "EN")
        ));
        return dto;
    }

    private AnswerTextDTO createAnswerText(String text, String language) {
        AnswerTextDTO dto = new AnswerTextDTO();
        dto.setAnswerText(text);
        dto.setLanguage(language);
        return dto;
    }
}