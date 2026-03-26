package com.auth.letter.controller;

import com.auth.letter.common.Result;
import com.auth.letter.entity.LookupValue;
import com.auth.letter.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 下拉值控制器
 */
@RestController
@RequestMapping("/lookup-values")
public class LookupController {

    @Autowired
    private LookupService lookupService;

    /**
     * 根据类型编码获取下拉值列表
     */
    @GetMapping("/{typeCode}")
    public Result<List<LookupValue>> getByTypeCode(@PathVariable String typeCode) {
        // 判断是否需要树形结构
        if (isTreeType(typeCode)) {
            List<LookupValue> result = lookupService.getTreeList(typeCode);
            return Result.success(result);
        } else {
            List<LookupValue> result = lookupService.getFlatList(typeCode);
            return Result.success(result);
        }
    }

    /**
     * 判断是否为树形类型
     */
    private boolean isTreeType(String typeCode) {
        return "APPLICABLE_REGION".equals(typeCode) ||
               "AUTH_PUBLISH_ORG".equals(typeCode) ||
               "INDUSTRY".equals(typeCode);
    }
}