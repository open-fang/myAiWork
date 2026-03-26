package com.auth.letter.dao;

import com.auth.letter.entity.RuleParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规则参数DAO接口
 */
public interface RuleParamDao {

    /**
     * 查询规则参数列表
     */
    List<RuleParam> selectList(@Param("name") String name,
                                @Param("nameEn") String nameEn,
                                @Param("status") String status,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);

    /**
     * 查询规则参数总数
     */
    long countList(@Param("name") String name,
                   @Param("nameEn") String nameEn,
                   @Param("status") String status);

    /**
     * 根据ID查询规则参数
     */
    RuleParam selectById(@Param("id") Long id);

    /**
     * 查询所有生效的规则参数
     */
    List<RuleParam> selectAllActive();

    /**
     * 插入规则参数
     */
    int insert(RuleParam ruleParam);

    /**
     * 更新规则参数
     */
    int update(RuleParam ruleParam);

    /**
     * 删除规则参数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updatedBy") String updatedBy);
}