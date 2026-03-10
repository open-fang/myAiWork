package com.auth.letter.repository;

import com.auth.letter.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> findBySceneId(Long sceneId);

    List<Rule> findBySceneIdOrderByOrderIndexAsc(Long sceneId);
}