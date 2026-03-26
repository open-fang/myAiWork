package com.auth.letter.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 授权书实体类
 */
public class AuthLetter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String authObjectLevel;
    private String applicableRegion;
    private String authPublishLevel;
    private String authPublishOrg;
    private Integer publishYear;
    private String summary;
    private String status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthObjectLevel() {
        return authObjectLevel;
    }

    public void setAuthObjectLevel(String authObjectLevel) {
        this.authObjectLevel = authObjectLevel;
    }

    public String getApplicableRegion() {
        return applicableRegion;
    }

    public void setApplicableRegion(String applicableRegion) {
        this.applicableRegion = applicableRegion;
    }

    public String getAuthPublishLevel() {
        return authPublishLevel;
    }

    public void setAuthPublishLevel(String authPublishLevel) {
        this.authPublishLevel = authPublishLevel;
    }

    public String getAuthPublishOrg() {
        return authPublishOrg;
    }

    public void setAuthPublishOrg(String authPublishOrg) {
        this.authPublishOrg = authPublishOrg;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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