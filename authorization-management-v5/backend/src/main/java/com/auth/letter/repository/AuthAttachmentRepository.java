package com.auth.letter.repository;

import com.auth.letter.entity.AuthAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Attachment Repository
 */
@Repository
public interface AuthAttachmentRepository extends JpaRepository<AuthAttachment, Long> {

    List<AuthAttachment> findByAuthLetterIdOrderByCreatedAtDesc(Long authLetterId);

    Page<AuthAttachment> findByAuthLetterIdOrderByCreatedAtDesc(Long authLetterId, Pageable pageable);

    void deleteByAuthLetterIdAndIdIn(Long authLetterId, List<Long> ids);
}