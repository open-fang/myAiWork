package com.auth.management.service.impl;

import com.auth.management.dto.request.AnswerDTO;
import com.auth.management.dto.request.AnswerTextDTO;
import com.auth.management.dto.request.CreateQuestionRequest;
import com.auth.management.dto.request.QuestionTextDTO;
import com.auth.management.dto.response.*;
import com.auth.management.entity.QuestionnaireAnswer;
import com.auth.management.entity.QuestionnaireAnswerText;
import com.auth.management.entity.QuestionnaireQuestion;
import com.auth.management.entity.QuestionnaireQuestionText;
import com.auth.management.enums.Language;
import com.auth.management.exception.BusinessException;
import com.auth.management.exception.ErrorCode;
import com.auth.management.repository.QuestionnaireRepository;
import com.auth.management.service.QuestionnaireService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Questionnaire Service Implementation
 */
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResponse<QuestionListResponse> queryList(String questionCode, String questionText,
                                                         String language, String createdBy,
                                                         String createdTimeFrom, String createdTimeTo,
                                                         String updatedBy, String updatedTimeFrom,
                                                         String updatedTimeTo,
                                                         Integer pageNum, Integer pageSize) {
        Page<QuestionnaireQuestion> page = new Page<>(pageNum, pageSize);
        Page<QuestionnaireQuestion> result = questionnaireRepository.findQuestionPage(page,
                questionCode, questionText, language, createdBy,
                createdTimeFrom, createdTimeTo, updatedBy,
                updatedTimeFrom, updatedTimeTo);

        List<QuestionListResponse> list = new ArrayList<>();
        for (QuestionnaireQuestion question : result.getRecords()) {
            QuestionListResponse response = convertToListResponse(question, language);
            list.add(response);
        }

        return PageResponse.of(list, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public QuestionDetailResponse getDetail(Long id) {
        QuestionnaireQuestion question = questionnaireRepository.findQuestionById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Question not found");
        }
        return convertToDetailResponse(question);
    }

    @Override
    @Transactional
    public CreateQuestionResponse create(CreateQuestionRequest request) {
        // Generate question code
        String questionCode = generateQuestionCode();

        // Create question
        QuestionnaireQuestion question = new QuestionnaireQuestion();
        question.setQuestionCode(questionCode);
        questionnaireRepository.insertQuestion(question);

        // Create question texts
        for (QuestionTextDTO textDTO : request.getQuestionTexts()) {
            // Check language uniqueness
            if (questionnaireRepository.checkLanguageExists(question.getId(), textDTO.getLanguage(), null)) {
                throw new BusinessException(ErrorCode.QUESTION_TEXT_LANGUAGE_DUPLICATE,
                        "Question text for language " + textDTO.getLanguage() + " already exists");
            }
            QuestionnaireQuestionText text = new QuestionnaireQuestionText();
            text.setQuestionId(question.getId());
            text.setQuestionText(textDTO.getQuestionText());
            text.setLanguage(textDTO.getLanguage());
            questionnaireRepository.insertQuestionText(text);
        }

        // Create answers
        for (AnswerDTO answerDTO : request.getAnswers()) {
            QuestionnaireAnswer answer = new QuestionnaireAnswer();
            answer.setQuestionId(question.getId());
            answer.setAnswerCode(generateAnswerCode());
            answer.setSortOrder(answerDTO.getSortOrder());
            questionnaireRepository.insertAnswer(answer);

            // Create answer texts
            for (AnswerTextDTO answerTextDTO : answerDTO.getAnswerTexts()) {
                QuestionnaireAnswerText answerText = new QuestionnaireAnswerText();
                answerText.setAnswerId(answer.getId());
                answerText.setAnswerText(answerTextDTO.getAnswerText());
                answerText.setLanguage(answerTextDTO.getLanguage());
                questionnaireRepository.insertAnswerText(answerText);
            }
        }

        CreateQuestionResponse response = new CreateQuestionResponse();
        response.setId(question.getId());
        response.setQuestionCode(questionCode);
        return response;
    }

    @Override
    @Transactional
    public void update(Long id, CreateQuestionRequest request) {
        QuestionnaireQuestion question = questionnaireRepository.findQuestionById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Question not found");
        }

        // Delete existing texts and answers
        questionnaireRepository.deleteQuestionTexts(id);
        List<QuestionnaireAnswer> existingAnswers = questionnaireRepository.findAnswers(id);
        List<Long> answerIds = existingAnswers.stream()
                .map(QuestionnaireAnswer::getId)
                .collect(Collectors.toList());
        questionnaireRepository.deleteAnswerTextsByAnswerIds(answerIds);
        questionnaireRepository.deleteAnswers(id);

        // Create new question texts
        for (QuestionTextDTO textDTO : request.getQuestionTexts()) {
            QuestionnaireQuestionText text = new QuestionnaireQuestionText();
            text.setQuestionId(id);
            text.setQuestionText(textDTO.getQuestionText());
            text.setLanguage(textDTO.getLanguage());
            questionnaireRepository.insertQuestionText(text);
        }

        // Create new answers
        for (AnswerDTO answerDTO : request.getAnswers()) {
            QuestionnaireAnswer answer = new QuestionnaireAnswer();
            answer.setQuestionId(id);
            answer.setAnswerCode(generateAnswerCode());
            answer.setSortOrder(answerDTO.getSortOrder());
            questionnaireRepository.insertAnswer(answer);

            for (AnswerTextDTO answerTextDTO : answerDTO.getAnswerTexts()) {
                QuestionnaireAnswerText answerText = new QuestionnaireAnswerText();
                answerText.setAnswerId(answer.getId());
                answerText.setAnswerText(answerTextDTO.getAnswerText());
                answerText.setLanguage(answerTextDTO.getLanguage());
                questionnaireRepository.insertAnswerText(answerText);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Delete answers and texts first
        List<QuestionnaireAnswer> answers = questionnaireRepository.findAnswers(id);
        List<Long> answerIds = answers.stream()
                .map(QuestionnaireAnswer::getId)
                .collect(Collectors.toList());
        questionnaireRepository.deleteAnswerTextsByAnswerIds(answerIds);
        questionnaireRepository.deleteAnswers(id);
        questionnaireRepository.deleteQuestionTexts(id);
        questionnaireRepository.deleteQuestion(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public List<QuestionOptionResponse> getOptions(String language) {
        String queryLanguage = (language != null && !language.isEmpty()) ? language : Language.ZH.getCode();
        List<QuestionnaireQuestion> questions = questionnaireRepository.findAllQuestions();

        List<QuestionOptionResponse> options = new ArrayList<>();
        for (QuestionnaireQuestion question : questions) {
            QuestionOptionResponse option = convertToOptionResponse(question, queryLanguage);
            options.add(option);
        }
        return options;
    }

    private String generateQuestionCode() {
        return "QT" + LocalDateTime.now().format(DATE_FORMATTER) +
                String.format("%03d", System.currentTimeMillis() % 1000);
    }

    private String generateAnswerCode() {
        return "ANS" + LocalDateTime.now().format(DATE_FORMATTER) +
                String.format("%03d", System.currentTimeMillis() % 1000);
    }

    private QuestionListResponse convertToListResponse(QuestionnaireQuestion question, String language) {
        QuestionListResponse response = new QuestionListResponse();
        response.setId(question.getId());
        response.setQuestionCode(question.getQuestionCode());
        response.setCreatedBy(question.getCreatedBy());
        response.setCreatedTime(question.getCreatedTime());
        response.setUpdatedBy(question.getUpdatedBy());
        response.setUpdatedTime(question.getUpdatedTime());

        // Get question text for language
        List<QuestionnaireQuestionText> texts = questionnaireRepository.findQuestionTexts(question.getId());
        String queryLanguage = (language != null && !language.isEmpty()) ? language : Language.ZH.getCode();
        for (QuestionnaireQuestionText text : texts) {
            if (queryLanguage.equals(text.getLanguage())) {
                response.setQuestionText(text.getQuestionText());
                response.setLanguage(text.getLanguage());
                break;
            }
        }

        return response;
    }

    private QuestionDetailResponse convertToDetailResponse(QuestionnaireQuestion question) {
        QuestionDetailResponse response = new QuestionDetailResponse();
        response.setId(question.getId());
        response.setQuestionCode(question.getQuestionCode());

        // Get question texts
        List<QuestionnaireQuestionText> texts = questionnaireRepository.findQuestionTexts(question.getId());
        List<QuestionTextResponse> textResponses = texts.stream()
                .map(t -> {
                    QuestionTextResponse tr = new QuestionTextResponse();
                    tr.setId(t.getId());
                    tr.setQuestionText(t.getQuestionText());
                    tr.setLanguage(t.getLanguage());
                    tr.setCreatedBy(t.getCreatedBy());
                    tr.setCreatedTime(t.getCreatedTime());
                    return tr;
                })
                .collect(Collectors.toList());
        response.setQuestionTexts(textResponses);

        // Get answers
        List<QuestionnaireAnswer> answers = questionnaireRepository.findAnswers(question.getId());
        List<AnswerResponse> answerResponses = answers.stream()
                .map(a -> {
                    AnswerResponse ar = new AnswerResponse();
                    ar.setId(a.getId());
                    ar.setAnswerCode(a.getAnswerCode());
                    ar.setSortOrder(a.getSortOrder());

                    List<QuestionnaireAnswerText> answerTexts = questionnaireRepository.findAnswerTexts(a.getId());
                    List<AnswerTextResponse> atrList = answerTexts.stream()
                            .map(at -> {
                                AnswerTextResponse atr = new AnswerTextResponse();
                                atr.setAnswerText(at.getAnswerText());
                                atr.setLanguage(at.getLanguage());
                                return atr;
                            })
                            .collect(Collectors.toList());
                    ar.setAnswerTexts(atrList);
                    return ar;
                })
                .collect(Collectors.toList());
        response.setAnswers(answerResponses);

        return response;
    }

    private QuestionOptionResponse convertToOptionResponse(QuestionnaireQuestion question, String language) {
        QuestionOptionResponse response = new QuestionOptionResponse();
        response.setQuestionId(question.getId());
        response.setQuestionCode(question.getQuestionCode());

        // Get question text
        List<QuestionnaireQuestionText> texts = questionnaireRepository.findQuestionTexts(question.getId());
        for (QuestionnaireQuestionText text : texts) {
            if (language.equals(text.getLanguage())) {
                response.setQuestionText(text.getQuestionText());
                break;
            }
        }

        // Get answers
        List<QuestionnaireAnswer> answers = questionnaireRepository.findAnswers(question.getId());
        List<QuestionOptionResponse.AnswerOptionResponse> answerOptions = answers.stream()
                .map(a -> {
                    QuestionOptionResponse.AnswerOptionResponse ao = new QuestionOptionResponse.AnswerOptionResponse();
                    ao.setAnswerId(a.getId());
                    ao.setAnswerCode(a.getAnswerCode());

                    List<QuestionnaireAnswerText> answerTexts = questionnaireRepository.findAnswerTexts(a.getId());
                    for (QuestionnaireAnswerText at : answerTexts) {
                        if (language.equals(at.getLanguage())) {
                            ao.setAnswerText(at.getAnswerText());
                            break;
                        }
                    }
                    return ao;
                })
                .collect(Collectors.toList());
        response.setAnswers(answerOptions);

        return response;
    }
}