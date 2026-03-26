package com.auth.letter.dto.request;

/**
 * 题目文本请求
 */
public class QuestionTextRequest {

    private String language;
    private String questionText;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}