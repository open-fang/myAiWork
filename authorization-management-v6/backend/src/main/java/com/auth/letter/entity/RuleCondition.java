package com.auth.letter.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 规则条件实体类
 */
public class RuleCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long ruleId;
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
    private String createdBy;
    private Timestamp createdTime;
    private String updatedBy;
    private Timestamp updatedTime;
    private Integer deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}