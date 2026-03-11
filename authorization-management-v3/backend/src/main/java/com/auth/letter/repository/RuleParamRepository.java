package com.auth.letter.repository;

import com.auth.letter.entity.RuleParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 规则参数Repository
 */
public interface RuleParamRepository extends JpaRepository<RuleParam, Long>, JpaSpecificationExecutor<RuleParam> {

    /**
     * 批量激活
     */
    @Modifying
    @Query("UPDATE RuleParam r SET r.status = 'ACTIVE', r.updatedAt = CURRENT_TIMESTAMP WHERE r.id IN :ids AND r.status = 'INACTIVE'")
    int batchActivate(@Param("ids") List<Long> ids);

    /**
     * 批量失效
     */
    @Modifying
    @Query("UPDATE RuleParam r SET r.status = 'INACTIVE', r.updatedAt = CURRENT_TIMESTAMP WHERE r.id IN :ids AND r.status = 'ACTIVE'")
    int batchDeactivate(@Param("ids") List<Long> ids);
}