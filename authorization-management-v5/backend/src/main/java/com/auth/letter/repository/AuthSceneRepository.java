package com.auth.letter.repository;

import com.auth.letter.entity.AuthScene;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Scene Repository
 */
@Repository
public interface AuthSceneRepository extends JpaRepository<AuthScene, Long> {

    List<AuthScene> findByAuthLetterIdOrderByCreatedAtDesc(Long authLetterId);

    Page<AuthScene> findByAuthLetterIdOrderByCreatedAtDesc(Long authLetterId, Pageable pageable);

    void deleteByAuthLetterIdAndIdIn(Long authLetterId, List<Long> ids);

    List<AuthScene> findByAuthLetterId(Long authLetterId);
}