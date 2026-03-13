package com.auth.letter.repository;

import com.auth.letter.entity.AuthLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Lookup Repository
 */
@Repository
public interface AuthLookupRepository extends JpaRepository<AuthLookup, Long> {

    List<AuthLookup> findByLookupTypeAndStatusOrderBySortOrderAsc(String lookupType, String status);

    List<AuthLookup> findByLookupTypeOrderBySortOrderAsc(String lookupType);

    List<AuthLookup> findByLookupTypeAndParentCodeOrderBySortOrderAsc(String lookupType, String parentCode);
}