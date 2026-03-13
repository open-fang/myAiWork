package com.auth.letter.repository;

import com.auth.letter.entity.AuthOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Operation Log Repository
 */
@Repository
public interface AuthOperationLogRepository extends JpaRepository<AuthOperationLog, Long> {

    Page<AuthOperationLog> findByAuthLetterIdOrderByOperatedAtDesc(Long authLetterId, Pageable pageable);

    List<AuthOperationLog> findByAuthLetterIdOrderByOperatedAtDesc(Long authLetterId);
}