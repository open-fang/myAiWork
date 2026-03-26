package com.auth.letter.dto.request;

import java.io.Serializable;
import java.util.List;

/**
 * 授权书创建/更新请求
 */
public class AuthLetterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<String> authObjectLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String summary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAuthObjectLevel() {
        return authObjectLevel;
    }

    public void setAuthObjectLevel(List<String> authObjectLevel) {
        this.authObjectLevel = authObjectLevel;
    }

    public List<String> getApplicableRegion() {
        return applicableRegion;
    }

    public void setApplicableRegion(List<String> applicableRegion) {
        this.applicableRegion = applicableRegion;
    }

    public List<String> getAuthPublishLevel() {
        return authPublishLevel;
    }

    public void setAuthPublishLevel(List<String> authPublishLevel) {
        this.authPublishLevel = authPublishLevel;
    }

    public List<String> getAuthPublishOrg() {
        return authPublishOrg;
    }

    public void setAuthPublishOrg(List<String> authPublishOrg) {
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
}