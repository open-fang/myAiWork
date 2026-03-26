package com.auth.letter.dto.request;

import java.util.List;

/**
 * 创建/更新答案请求
 */
public class AnswerRequest {

    private List<AnswerTextRequest> answerTexts;
    private Integer sortOrder;

    public List<AnswerTextRequest> getAnswerTexts() {
        return answerTexts;
    }

    public void setAnswerTexts(List<AnswerTextRequest> answerTexts) {
        this.answerTexts = answerTexts;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}