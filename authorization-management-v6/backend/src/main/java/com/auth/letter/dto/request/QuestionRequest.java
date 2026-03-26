package com.auth.letter.dto.request;

import java.util.List;

/**
 * 创建/更新题目请求
 */
public class QuestionRequest {

    private List<QuestionTextRequest> questionTexts;
    private String status;

    public List<QuestionTextRequest> getQuestionTexts() {
        return questionTexts;
    }

    public void setQuestionTexts(List<QuestionTextRequest> questionTexts) {
        this.questionTexts = questionTexts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}