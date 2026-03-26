package com.auth.letter.dto.request;

import java.io.Serializable;
import java.util.Map;

/**
 * 场景匹配请求
 */
public class SceneMatchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long authLetterId;
    private Map<String, Object> data;

    public Long getAuthLetterId() {
        return authLetterId;
    }

    public void setAuthLetterId(Long authLetterId) {
        this.authLetterId = authLetterId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}