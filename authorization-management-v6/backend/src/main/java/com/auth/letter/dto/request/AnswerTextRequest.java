package com.auth.letter.dto.request;

/**
 * 答案文本请求
 */
public class AnswerTextRequest {

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