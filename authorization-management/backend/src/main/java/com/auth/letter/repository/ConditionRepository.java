package com.auth.letter.repository;

import com.auth.letter.entity.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {

    List<Condition> findByRuleId(Long ruleId);

    List<Condition> findByParentId(Long parentId);
}