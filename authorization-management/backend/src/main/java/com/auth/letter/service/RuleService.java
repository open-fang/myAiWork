package com.auth.letter.service;

import com.auth.letter.dto.ConditionDTO;
import com.auth.letter.dto.RuleDTO;
import com.auth.letter.entity.Condition;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.Scene;
import com.auth.letter.exception.BusinessException;
import com.auth.letter.repository.ConditionRepository;
import com.auth.letter.repository.RuleRepository;
import com.auth.letter.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;
    private final SceneRepository sceneRepository;
    private final ConditionRepository conditionRepository;

    /**
     * Create a new rule under a scene
     */
    @Transactional
    public RuleDTO create(Long sceneId, RuleDTO dto) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new BusinessException("Scene not found: " + sceneId));

        Rule rule = new Rule();
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0);
        rule.setScene(scene);

        Rule saved = ruleRepository.save(rule);

        // Create conditions if provided
        if (dto.getConditions() != null && !dto.getConditions().isEmpty()) {
            createConditions(saved, dto.getConditions(), null);
        }

        return toDTO(saved);
    }

    /**
     * Update a rule
     */
    @Transactional
    public RuleDTO update(Long id, RuleDTO dto) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Rule not found: " + id));

        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setOrderIndex(dto.getOrderIndex());

        Rule saved = ruleRepository.save(rule);
        return toDTO(saved);
    }

    /**
     * Get rule by ID
     */
    public RuleDTO getById(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Rule not found: " + id));
        return toDTO(rule);
    }

    /**
     * Get all rules by scene ID
     */
    public List<RuleDTO> getBySceneId(Long sceneId) {
        return ruleRepository.findBySceneIdOrderByOrderIndexAsc(sceneId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete a rule
     */
    @Transactional
    public void delete(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Rule not found: " + id));

        ruleRepository.delete(rule);
    }

    /**
     * Add condition to a rule
     */
    @Transactional
    public ConditionDTO addCondition(Long ruleId, ConditionDTO dto) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new BusinessException("Rule not found: " + ruleId));

        Condition condition = createCondition(rule, dto, null);
        Condition saved = conditionRepository.save(condition);
        return toDTO(saved);
    }

    /**
     * Add nested condition
     */
    @Transactional
    public ConditionDTO addNestedCondition(Long parentConditionId, ConditionDTO dto) {
        Condition parent = conditionRepository.findById(parentConditionId)
                .orElseThrow(() -> new BusinessException("Condition not found: " + parentConditionId));

        Condition condition = createCondition(parent.getRule(), dto, parent);
        Condition saved = conditionRepository.save(condition);
        return toDTO(saved);
    }

    /**
     * Delete a condition
     */
    @Transactional
    public void deleteCondition(Long conditionId) {
        Condition condition = conditionRepository.findById(conditionId)
                .orElseThrow(() -> new BusinessException("Condition not found: " + conditionId));

        conditionRepository.delete(condition);
    }

    private void createConditions(Rule rule, List<ConditionDTO> dtos, Condition parent) {
        if (dtos == null) return;

        for (ConditionDTO dto : dtos) {
            Condition condition = createCondition(rule, dto, parent);
            Condition saved = conditionRepository.save(condition);

            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                createConditions(rule, dto.getChildren(), saved);
            }
        }
    }

    private Condition createCondition(Rule rule, ConditionDTO dto, Condition parent) {
        Condition condition = new Condition();
        condition.setRule(rule);
        condition.setParent(parent);
        condition.setLogicOperator(dto.getLogicOperator());
        condition.setFieldName(dto.getFieldName());
        condition.setOperator(dto.getOperator());
        condition.setValue(dto.getValue());
        return condition;
    }

    private RuleDTO toDTO(Rule rule) {
        RuleDTO dto = new RuleDTO();
        dto.setId(rule.getId());
        dto.setName(rule.getName());
        dto.setDescription(rule.getDescription());
        dto.setOrderIndex(rule.getOrderIndex());
        dto.setCreatedAt(rule.getCreatedAt());
        dto.setUpdatedAt(rule.getUpdatedAt());
        dto.setSceneId(rule.getScene().getId());

        List<Condition> conditions = conditionRepository.findByRuleId(rule.getId());
        dto.setConditions(buildConditionTree(conditions, null));

        return dto;
    }

    private ConditionDTO toDTO(Condition condition) {
        ConditionDTO dto = new ConditionDTO();
        dto.setId(condition.getId());
        dto.setRuleId(condition.getRule() != null ? condition.getRule().getId() : null);
        dto.setParentId(condition.getParent() != null ? condition.getParent().getId() : null);
        dto.setLogicOperator(condition.getLogicOperator());
        dto.setFieldName(condition.getFieldName());
        dto.setOperator(condition.getOperator());
        dto.setValue(condition.getValue());
        dto.setCreatedAt(condition.getCreatedAt());
        return dto;
    }

    private List<ConditionDTO> buildConditionTree(List<Condition> conditions, Long parentId) {
        return conditions.stream()
                .filter(c -> (parentId == null && c.getParent() == null) ||
                             (c.getParent() != null && c.getParent().getId().equals(parentId)))
                .map(c -> {
                    ConditionDTO dto = toDTO(c);
                    dto.setChildren(buildConditionTree(conditions, c.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}