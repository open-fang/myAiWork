package com.auth.letter.dto.request;

import java.io.Serializable;
import java.util.List;

/**
 * 授权书查询请求
 */
public class AuthLetterQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<String> authObjectLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}