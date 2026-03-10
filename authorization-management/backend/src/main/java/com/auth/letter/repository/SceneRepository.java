package com.auth.letter.repository;

import com.auth.letter.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {

    List<Scene> findByAuthLetterId(Long authLetterId);

    List<Scene> findByAuthLetterIdOrderByOrderIndexAsc(Long authLetterId);
}