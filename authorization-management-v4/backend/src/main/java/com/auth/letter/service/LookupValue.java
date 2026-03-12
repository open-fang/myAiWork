package com.auth.letter.service;

import lombok.Data;
import java.util.List;

@Data
public class LookupValue {
    private String code;
    private String name;
    private List<LookupValue> children;

    public LookupValue() {}

    public LookupValue(String code, String name, List<LookupValue> children) {
        this.code = code;
        this.name = name;
        this.children = children;
    }
}