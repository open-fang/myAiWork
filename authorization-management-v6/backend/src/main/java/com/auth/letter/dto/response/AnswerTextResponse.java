package com.auth.letter.dto.response;

/**
 * 答案文本响应
 */
public class AnswerTextResponse {

    private String language;
    private String answerText;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}