package com.auth.letter.dto.request;

import java.io.Serializable;
import java.util.List;

/**
 * 场景请求
 */
public class SceneRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sceneName;
    private List<String> industry;
    private String businessScene;
    private String decisionLevel;
    private String specificRule;
    private List<RuleDto> rules;
    private List<QuestionDto> questionnaire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public List<String> getIndustry() {
        return industry;
    }

    public void setIndustry(List<String> industry) {
        this.industry = industry;
    }

    public String getBusinessScene() {
        return businessScene;
    }

    public void setBusinessScene(String businessScene) {
        this.businessScene = businessScene;
    }

    public String getDecisionLevel() {
        return decisionLevel;
    }

    public void setDecisionLevel(String decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    public String getSpecificRule() {
        return specificRule;
    }

    public void setSpecificRule(String specificRule) {
        this.specificRule = specificRule;
    }

    public List<RuleDto> getRules() {
        return rules;
    }

    public void setRules(List<RuleDto> rules) {
        this.rules = rules;
    }

    public List<QuestionDto> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(List<QuestionDto> questionnaire) {
        this.questionnaire = questionnaire;
    }

    /**
     * 规则DTO
     */
    public static class RuleDto implements Serializable {
        private static final long serialVersionUID = 1L;
        private String logicType;
        private List<ConditionDto> conditions;

        public String getLogicType() {
            return logicType;
        }

        public void setLogicType(String logicType) {
            this.logicType = logicType;
        }

        public List<ConditionDto> getConditions() {
            return conditions;
        }

        public void setConditions(List<ConditionDto> conditions) {
            this.conditions = conditions;
        }
    }

    /**
     * 条件DTO
     */
    public static class ConditionDto implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long parentConditionId;
        private String fieldCode;
        private String operator;
        private String compareType;
        private String compareFieldCode;
        private String compareValue;
        private String compareUnit;
        private String logicType;
        private Integer isGroup;
        private String groupLogicType;
        private Integer sortOrder;

        public Long getParentConditionId() {
            return parentConditionId;
        }

        public void setParentConditionId(Long parentConditionId) {
            this.parentConditionId = parentConditionId;
        }

        public String getFieldCode() {
            return fieldCode;
        }

        public void setFieldCode(String fieldCode) {
            this.fieldCode = fieldCode;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getCompareType() {
            return compareType;
        }

        public void setCompareType(String compareType) {
            this.compareType = compareType;
        }

        public String getCompareFieldCode() {
            return compareFieldCode;
        }

        public void setCompareFieldCode(String compareFieldCode) {
            this.compareFieldCode = compareFieldCode;
        }

        public String getCompareValue() {
            return compareValue;
        }

        public void setCompareValue(String compareValue) {
            this.compareValue = compareValue;
        }

        public String getCompareUnit() {
            return compareUnit;
        }

        public void setCompareUnit(String compareUnit) {
            this.compareUnit = compareUnit;
        }

        public String getLogicType() {
            return logicType;
        }

        public void setLogicType(String logicType) {
            this.logicType = logicType;
        }

        public Integer getIsGroup() {
            return isGroup;
        }

        public void setIsGroup(Integer isGroup) {
            this.isGroup = isGroup;
        }

        public String getGroupLogicType() {
            return groupLogicType;
        }

        public void setGroupLogicType(String groupLogicType) {
            this.groupLogicType = groupLogicType;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }
    }

    /**
     * 问题DTO
     */
    public static class QuestionDto implements Serializable {
        private static final long serialVersionUID = 1L;
        private String question;
        private List<String> options;
        private String answer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}