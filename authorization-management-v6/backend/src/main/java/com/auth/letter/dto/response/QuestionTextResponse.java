package com.auth.letter.dto.response;

/**
 * 题目文本响应
 */
public class QuestionTextResponse {

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