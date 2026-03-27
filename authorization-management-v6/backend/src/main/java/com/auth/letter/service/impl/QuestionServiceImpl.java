package com.auth.letter.service.impl;

import com.auth.letter.common.PageResult;
import com.auth.letter.dao.AnswerDao;
import com.auth.letter.dao.QuestionDao;
import com.auth.letter.dto.request.AnswerRequest;
import com.auth.letter.dto.request.AnswerTextRequest;
import com.auth.letter.dto.request.QuestionQueryRequest;
import com.auth.letter.dto.request.QuestionRequest;
import com.auth.letter.dto.request.QuestionTextRequest;
import com.auth.letter.dto.response.AnswerResponse;
import com.auth.letter.dto.response.AnswerTextResponse;
import com.auth.letter.dto.response.QuestionResponse;
import com.auth.letter.dto.response.QuestionTextResponse;
import com.auth.letter.entity.Answer;
import com.auth.letter.entity.AnswerText;
import com.auth.letter.entity.Question;
import com.auth.letter.entity.QuestionText;
import com.auth.letter.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 问卷题目服务实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public PageResult<QuestionResponse> list(QuestionQueryRequest request) {
        List<Question> questions = questionDao.selectList(
                request.getQuestionCode(),
                request.getQuestionText(),
                request.getLanguage(),
                request.getCreatedBy(),
                request.getCreatedDate(),
                request.getUpdatedBy(),
                request.getUpdatedDate(),
                request.getOffset(),
                request.getPageSize()
        );

        long total = questionDao.countList(
                request.getQuestionCode(),
                request.getQuestionText(),
                request.getLanguage(),
                request.getCreatedBy(),
                request.getCreatedDate(),
                request.getUpdatedBy(),
                request.getUpdatedDate()
        );

        List<QuestionResponse> responseList = new ArrayList<>();
        for (Question question : questions) {
            responseList.add(convertToResponse(question));
        }

        return new PageResult<>(responseList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public QuestionResponse getById(Long id) {
        Question question = questionDao.selectById(id);
        if (question == null) {
            return null;
        }
        return convertToResponse(question);
    }

    @Override
    @Transactional
    public Long create(QuestionRequest request, String operator) {
        // 验证语言唯一性
        validateLanguageUniqueness(request.getQuestionTexts(), null);

        // 生成题目编号
        String questionCode = generateQuestionCode();

        // 创建题目主表
        Question question = new Question();
        question.setQuestionCode(questionCode);
        question.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        question.setCreatedBy(operator);
        questionDao.insert(question);

        // 创建题目文本
        if (request.getQuestionTexts() != null) {
            for (QuestionTextRequest textRequest : request.getQuestionTexts()) {
                QuestionText text = new QuestionText();
                text.setQuestionId(question.getId());
                text.setQuestionText(textRequest.getQuestionText());
                text.setLanguage(textRequest.getLanguage());
                text.setCreatedBy(operator);
                questionDao.insertText(text);
            }
        }

        return question.getId();
    }

    @Override
    @Transactional
    public void update(Long id, QuestionRequest request, String operator) {
        Question question = questionDao.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 验证语言唯一性
        validateLanguageUniqueness(request.getQuestionTexts(), id);

        // 更新题目主表
        if (request.getStatus() != null) {
            question.setStatus(request.getStatus());
        }
        question.setUpdatedBy(operator);
        questionDao.update(question);

        // 更新题目文本：先删除旧的，再插入新的
        questionDao.deleteTextsByQuestionId(id);
        if (request.getQuestionTexts() != null) {
            for (QuestionTextRequest textRequest : request.getQuestionTexts()) {
                QuestionText text = new QuestionText();
                text.setQuestionId(id);
                text.setQuestionText(textRequest.getQuestionText());
                text.setLanguage(textRequest.getLanguage());
                text.setCreatedBy(operator);
                questionDao.insertText(text);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id, String operator) {
        // 检查是否存在答案
        long answerCount = answerDao.countByQuestionId(id);
        if (answerCount > 0) {
            throw new RuntimeException("题目下存在答案，无法删除");
        }

        // 删除题目文本
        questionDao.deleteTextsByQuestionId(id);
        // 删除题目
        questionDao.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids, String operator) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 检查每个题目是否存在答案
        for (Long id : ids) {
            long answerCount = answerDao.countByQuestionId(id);
            if (answerCount > 0) {
                Question question = questionDao.selectById(id);
                throw new RuntimeException("题目[" + (question != null ? question.getQuestionCode() : id) + "]下存在答案，无法删除");
            }
        }

        // 删除题目文本和题目
        for (Long id : ids) {
            questionDao.deleteTextsByQuestionId(id);
        }
        questionDao.deleteByIds(ids);
    }

    @Override
    public PageResult<AnswerResponse> listAnswers(Long questionId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Answer> answers = answerDao.selectByQuestionId(questionId, offset, pageSize);
        long total = answerDao.countByQuestionId(questionId);

        List<AnswerResponse> responseList = new ArrayList<>();
        for (Answer answer : answers) {
            responseList.add(convertToAnswerResponse(answer));
        }

        return new PageResult<>(responseList, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public Long createAnswer(Long questionId, AnswerRequest request, String operator) {
        // 验证题目是否存在
        Question question = questionDao.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 生成答案编号
        String answerCode = generateAnswerCode();

        // 创建答案主表
        Answer answer = new Answer();
        answer.setAnswerCode(answerCode);
        answer.setQuestionId(questionId);
        answer.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        answer.setCreatedBy(operator);
        answerDao.insert(answer);

        // 创建答案文本
        if (request.getAnswerTexts() != null) {
            for (AnswerTextRequest textRequest : request.getAnswerTexts()) {
                AnswerText text = new AnswerText();
                text.setAnswerId(answer.getId());
                text.setAnswerText(textRequest.getAnswerText());
                text.setLanguage(textRequest.getLanguage());
                text.setCreatedBy(operator);
                answerDao.insertText(text);
            }
        }

        return answer.getId();
    }

    @Override
    @Transactional
    public void updateAnswer(Long id, AnswerRequest request, String operator) {
        Answer answer = answerDao.selectById(id);
        if (answer == null) {
            throw new RuntimeException("答案不存在");
        }

        // 更新答案主表
        if (request.getSortOrder() != null) {
            answer.setSortOrder(request.getSortOrder());
        }
        answer.setUpdatedBy(operator);
        answerDao.update(answer);

        // 更新答案文本：先删除旧的，再插入新的
        answerDao.deleteTextsByAnswerId(id);
        if (request.getAnswerTexts() != null) {
            for (AnswerTextRequest textRequest : request.getAnswerTexts()) {
                AnswerText text = new AnswerText();
                text.setAnswerId(id);
                text.setAnswerText(textRequest.getAnswerText());
                text.setLanguage(textRequest.getLanguage());
                text.setCreatedBy(operator);
                answerDao.insertText(text);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id, String operator) {
        // 删除答案文本
        answerDao.deleteTextsByAnswerId(id);
        // 删除答案
        answerDao.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDeleteAnswers(List<Long> ids, String operator) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 删除答案文本和答案
        for (Long id : ids) {
            answerDao.deleteTextsByAnswerId(id);
        }
        answerDao.deleteByIds(ids);
    }

    // ==================== 私有方法 ====================

    private QuestionResponse convertToResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestionCode(question.getQuestionCode());
        response.setStatus(question.getStatus());
        response.setCreatedBy(question.getCreatedBy());
        response.setCreatedTime(question.getCreatedTime());
        response.setUpdatedBy(question.getUpdatedBy());
        response.setUpdatedTime(question.getUpdatedTime());

        // 查询题目文本
        List<QuestionText> texts = questionDao.selectTextsByQuestionId(question.getId());
        List<QuestionTextResponse> textResponses = new ArrayList<>();
        for (QuestionText text : texts) {
            QuestionTextResponse textResponse = new QuestionTextResponse();
            textResponse.setLanguage(text.getLanguage());
            textResponse.setQuestionText(text.getQuestionText());
            textResponses.add(textResponse);
        }
        response.setQuestionTexts(textResponses);

        return response;
    }

    private AnswerResponse convertToAnswerResponse(Answer answer) {
        AnswerResponse response = new AnswerResponse();
        response.setId(answer.getId());
        response.setAnswerCode(answer.getAnswerCode());
        response.setSortOrder(answer.getSortOrder());
        response.setCreatedBy(answer.getCreatedBy());
        response.setCreatedTime(answer.getCreatedTime());
        response.setUpdatedBy(answer.getUpdatedBy());
        response.setUpdatedTime(answer.getUpdatedTime());

        // 查询答案文本
        List<AnswerText> texts = answerDao.selectTextsByAnswerId(answer.getId());
        List<AnswerTextResponse> textResponses = new ArrayList<>();
        for (AnswerText text : texts) {
            AnswerTextResponse textResponse = new AnswerTextResponse();
            textResponse.setLanguage(text.getLanguage());
            textResponse.setAnswerText(text.getAnswerText());
            textResponses.add(textResponse);
        }
        response.setAnswerTexts(textResponses);

        return response;
    }

    private String generateQuestionCode() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Timestamp(System.currentTimeMillis()));
        int seq = counter.getAndIncrement() % 1000;
        return String.format("QT%s%03d", timestamp, seq);
    }

    private String generateAnswerCode() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Timestamp(System.currentTimeMillis()));
        int seq = counter.getAndIncrement() % 1000;
        return String.format("ANS%s%03d", timestamp, seq);
    }

    private void validateLanguageUniqueness(List<QuestionTextRequest> texts, Long excludeQuestionId) {
        if (texts == null || texts.isEmpty()) {
            return;
        }

        for (QuestionTextRequest text : texts) {
            if (text.getLanguage() != null) {
                int count = questionDao.countTextByLanguage(excludeQuestionId, text.getLanguage(), null);
                // 如果是更新操作，需要排除当前文本
                if (count > 0) {
                    throw new RuntimeException("相同语言只能维护一个题目");
                }
            }
        }
    }

    }