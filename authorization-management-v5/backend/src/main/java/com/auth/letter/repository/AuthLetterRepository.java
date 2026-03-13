package com.auth.letter.repository;

import com.auth.letter.entity.AuthLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Authorization Letter Repository
 */
@Repository
public interface AuthLetterRepository extends JpaRepository<AuthLetter, Long> {

    Optional<AuthLetter> findByCode(String code);

    @Query("SELECT a FROM AuthLetter a WHERE " +
            "(:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:publishYear IS NULL OR a.publishYear = :publishYear) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:authTargetLevel IS NULL OR a.authTargetLevel LIKE CONCAT('%', :authTargetLevel, '%')) AND " +
            "(:applicableRegion IS NULL OR a.applicableRegion LIKE CONCAT('%', :applicableRegion, '%')) AND " +
            "(:authPublishLevel IS NULL OR a.authPublishLevel LIKE CONCAT('%', :authPublishLevel, '%')) AND " +
            "(:authPublishOrg IS NULL OR a.authPublishOrg LIKE CONCAT('%', :authPublishOrg, '%'))")
    Page<AuthLetter> findByConditions(
            @Param("name") String name,
            @Param("publishYear") Integer publishYear,
            @Param("status") String status,
            @Param("authTargetLevel") String authTargetLevel,
            @Param("applicableRegion") String applicableRegion,
            @Param("authPublishLevel") String authPublishLevel,
            @Param("authPublishOrg") String authPublishOrg,
            Pageable pageable);

    @Query("SELECT a FROM AuthLetter a WHERE a.id IN :ids AND a.status = :status")
    List<AuthLetter> findByIdsAndStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    boolean existsByCode(String code);
}