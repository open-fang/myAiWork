package com.auth.letter.repository;

import com.auth.letter.entity.AuthLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Authorization Letter Repository
 */
@Repository
public interface AuthLetterRepository extends JpaRepository<AuthLetter, Long>, JpaSpecificationExecutor<AuthLetter> {

    /**
     * Find by status
     */
    Page<AuthLetter> findByStatus(String status, Pageable pageable);

    /**
     * Find by name containing (case insensitive)
     */
    Page<AuthLetter> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find by publish year
     */
    Page<AuthLetter> findByPublishYear(Integer publishYear, Pageable pageable);

    /**
     * Find by created by
     */
    Page<AuthLetter> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * Batch update status to PUBLISHED
     */
    @Modifying
    @Query("UPDATE AuthLetter a SET a.status = 'PUBLISHED', a.publishedAt = CURRENT_TIMESTAMP WHERE a.id IN :ids AND a.status = 'DRAFT'")
    int batchPublish(@Param("ids") List<Long> ids);

    /**
     * Batch update status to EXPIRED
     */
    @Modifying
    @Query("UPDATE AuthLetter a SET a.status = 'EXPIRED' WHERE a.id IN :ids AND a.status = 'PUBLISHED'")
    int batchExpire(@Param("ids") List<Long> ids);

    /**
     * Batch delete by ids (only DRAFT status)
     */
    @Modifying
    @Query("DELETE FROM AuthLetter a WHERE a.id IN :ids AND a.status = 'DRAFT'")
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * Count by status and ids
     */
    @Query("SELECT COUNT(a) FROM AuthLetter a WHERE a.id IN :ids AND a.status = :status")
    long countByIdsAndStatus(@Param("ids") List<Long> ids, @Param("status") String status);
}