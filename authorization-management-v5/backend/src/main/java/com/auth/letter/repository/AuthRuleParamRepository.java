package com.auth.letter.repository;

import com.auth.letter.entity.AuthRuleParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Rule Parameter Repository
 */
@Repository
public interface AuthRuleParamRepository extends JpaRepository<AuthRuleParam, Long> {

    @Query("SELECT r FROM AuthRuleParam r WHERE " +
            "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:nameEn IS NULL OR LOWER(r.nameEn) LIKE LOWER(CONCAT('%', :nameEn, '%'))) AND " +
            "(:status IS NULL OR r.status = :status)")
    Page<AuthRuleParam> findByConditions(
            @Param("name") String name,
            @Param("nameEn") String nameEn,
            @Param("status") String status,
            Pageable pageable);

    List<AuthRuleParam> findByStatusOrderByCreatedAtDesc(String status);

    boolean existsByNameEn(String nameEn);
}