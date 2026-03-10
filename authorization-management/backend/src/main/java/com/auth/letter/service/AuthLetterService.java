package com.auth.letter.service;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.Condition;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import com.auth.letter.exception.BusinessException;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.repository.ConditionRepository;
import com.auth.letter.repository.RuleRepository;
import com.auth.letter.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthLetterService {

    private final AuthLetterRepository authLetterRepository;
    private final SceneRepository sceneRepository;
    private final RuleRepository ruleRepository;
    private final ConditionRepository conditionRepository;
    private final ConditionEvaluator conditionEvaluator;

    /**
     * Create a new authorization letter
     */
    @Transactional
    public AuthLetterDTO create(AuthLetterDTO dto) {
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName(dto.getName());
        authLetter.setDescription(dto.getDescription());
        authLetter.setStatus(AuthLetterStatus.DRAFT);
        authLetter.setPublishLevel(dto.getPublishLevel());
        authLetter.setAuthorizedLevel(dto.getAuthorizedLevel());
        authLetter.setValidFrom(dto.getValidFrom());
        authLetter.setValidTo(dto.getValidTo());

        AuthLetter saved = authLetterRepository.save(authLetter);
        return toDTO(saved);
    }

    /**
     * Update an existing authorization letter
     */
    @Transactional
    public AuthLetterDTO update(Long id, AuthLetterDTO dto) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));

        if (authLetter.getStatus() == AuthLetterStatus.PUBLISHED) {
            throw new BusinessException("Cannot update published authorization letter");
        }

        authLetter.setName(dto.getName());
        authLetter.setDescription(dto.getDescription());
        authLetter.setPublishLevel(dto.getPublishLevel());
        authLetter.setAuthorizedLevel(dto.getAuthorizedLevel());
        authLetter.setValidFrom(dto.getValidFrom());
        authLetter.setValidTo(dto.getValidTo());

        AuthLetter saved = authLetterRepository.save(authLetter);
        return toDTO(saved);
    }

    /**
     * Get authorization letter by ID
     */
    public AuthLetterDTO getById(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));
        return toDTOWithDetails(authLetter);
    }

    /**
     * Get all authorization letters
     */
    public List<AuthLetterDTO> getAll() {
        return authLetterRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete an authorization letter
     */
    @Transactional
    public void delete(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));

        if (authLetter.getStatus() == AuthLetterStatus.PUBLISHED) {
            throw new BusinessException("Cannot delete published authorization letter");
        }

        authLetterRepository.delete(authLetter);
    }

    /**
     * Publish an authorization letter
     */
    @Transactional
    public AuthLetterDTO publish(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));

        if (authLetter.getStatus() == AuthLetterStatus.PUBLISHED) {
            throw new BusinessException("Authorization letter is already published");
        }

        authLetter.setStatus(AuthLetterStatus.PUBLISHED);
        authLetter.setPublishedAt(LocalDateTime.now());

        AuthLetter saved = authLetterRepository.save(authLetter);
        return toDTO(saved);
    }

    /**
     * Expire authorization letters that have passed their valid date
     */
    @Transactional
    public void expireLetters() {
        List<AuthLetter> expiredLetters = authLetterRepository.findExpiredLetters(LocalDateTime.now());
        for (AuthLetter letter : expiredLetters) {
            letter.setStatus(AuthLetterStatus.EXPIRED);
            authLetterRepository.save(letter);
        }
    }

    /**
     * Match scenes based on request
     */
    public MatchResult match(MatchRequest request) {
        AuthLetter authLetter = authLetterRepository.findById(request.getAuthLetterId())
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + request.getAuthLetterId()));

        if (!authLetter.isValid()) {
            return new MatchResult(false, null, null, null, Collections.emptyList());
        }

        List<Scene> scenes = sceneRepository.findByAuthLetterIdOrderByOrderIndexAsc(authLetter.getId());
        List<MatchResult.SceneMatch> matchedScenes = new ArrayList<>();

        for (Scene scene : scenes) {
            List<Rule> rules = ruleRepository.findBySceneIdOrderByOrderIndexAsc(scene.getId());
            List<MatchResult.RuleMatch> matchedRules = new ArrayList<>();

            for (Rule rule : rules) {
                List<Condition> conditions = conditionRepository.findByRuleId(rule.getId());
                if (matchConditions(conditions, request.getFields())) {
                    matchedRules.add(new MatchResult.RuleMatch(rule.getId(), rule.getName()));
                }
            }

            if (!matchedRules.isEmpty()) {
                matchedScenes.add(new MatchResult.SceneMatch(
                        scene.getId(),
                        scene.getName(),
                        scene.getDecisionLevel(),
                        matchedRules
                ));
            }
        }

        if (!matchedScenes.isEmpty()) {
            MatchResult.SceneMatch firstMatch = matchedScenes.get(0);
            return new MatchResult(
                    true,
                    firstMatch.getSceneName(),
                    firstMatch.getMatchedRules().isEmpty() ? null : firstMatch.getMatchedRules().get(0).getRuleName(),
                    firstMatch.getDecisionLevel(),
                    matchedScenes
            );
        }

        return new MatchResult(false, null, null, null, Collections.emptyList());
    }

    /**
     * Check if conditions match the provided fields
     */
    private boolean matchConditions(List<Condition> conditions, Map<String, Object> fields) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        // Get root conditions (no parent)
        List<Condition> rootConditions = conditions.stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());

        if (rootConditions.isEmpty()) {
            return true;
        }

        return conditionEvaluator.evaluate(rootConditions, fields);
    }

    private AuthLetterDTO toDTO(AuthLetter authLetter) {
        AuthLetterDTO dto = new AuthLetterDTO();
        dto.setId(authLetter.getId());
        dto.setName(authLetter.getName());
        dto.setDescription(authLetter.getDescription());
        dto.setStatus(authLetter.getStatus());
        dto.setPublishLevel(authLetter.getPublishLevel());
        dto.setAuthorizedLevel(authLetter.getAuthorizedLevel());
        dto.setValidFrom(authLetter.getValidFrom());
        dto.setValidTo(authLetter.getValidTo());
        dto.setCreatedAt(authLetter.getCreatedAt());
        dto.setUpdatedAt(authLetter.getUpdatedAt());
        dto.setPublishedAt(authLetter.getPublishedAt());
        return dto;
    }

    private AuthLetterDTO toDTOWithDetails(AuthLetter authLetter) {
        AuthLetterDTO dto = toDTO(authLetter);
        List<Scene> scenes = sceneRepository.findByAuthLetterIdOrderByOrderIndexAsc(authLetter.getId());
        dto.setScenes(scenes.stream().map(this::toDTO).collect(Collectors.toList()));
        return dto;
    }

    private SceneDTO toDTO(Scene scene) {
        SceneDTO dto = new SceneDTO();
        dto.setId(scene.getId());
        dto.setName(scene.getName());
        dto.setDescription(scene.getDescription());
        dto.setDecisionLevel(scene.getDecisionLevel());
        dto.setOrderIndex(scene.getOrderIndex());
        dto.setCreatedAt(scene.getCreatedAt());
        dto.setUpdatedAt(scene.getUpdatedAt());
        dto.setAuthLetterId(scene.getAuthLetter().getId());

        List<Rule> rules = ruleRepository.findBySceneIdOrderByOrderIndexAsc(scene.getId());
        dto.setRules(rules.stream().map(this::toDTO).collect(Collectors.toList()));

        return dto;
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

    /**
     * Build condition tree from flat list
     */
    private List<ConditionDTO> buildConditionTree(List<Condition> conditions, Long parentId) {
        return conditions.stream()
                .filter(c -> (parentId == null && c.getParent() == null) ||
                             (c.getParent() != null && c.getParent().getId().equals(parentId)))
                .map(c -> {
                    ConditionDTO dto = new ConditionDTO();
                    dto.setId(c.getId());
                    dto.setRuleId(c.getRule() != null ? c.getRule().getId() : null);
                    dto.setParentId(parentId);
                    dto.setLogicOperator(c.getLogicOperator());
                    dto.setFieldName(c.getFieldName());
                    dto.setOperator(c.getOperator());
                    dto.setValue(c.getValue());
                    dto.setCreatedAt(c.getCreatedAt());
                    dto.setChildren(buildConditionTree(conditions, c.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}