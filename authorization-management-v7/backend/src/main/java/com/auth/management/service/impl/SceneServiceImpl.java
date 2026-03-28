package com.auth.management.service.impl;

import com.auth.management.dto.request.CreateSceneRequest;
import com.auth.management.dto.request.RuleConditionDTO;
import com.auth.management.dto.request.RuleGroupDTO;
import com.auth.management.dto.request.SceneQuestionnaireDTO;
import com.auth.management.dto.response.*;
import com.auth.management.entity.*;
import com.auth.management.exception.BusinessException;
import com.auth.management.exception.ErrorCode;
import com.auth.management.repository.*;
import com.auth.management.service.SceneService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scene Service Implementation
 */
@Service
public class SceneServiceImpl implements SceneService {

    @Autowired
    private AuthLetterSceneRepository sceneRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private SceneQuestionnaireRepository sceneQuestionnaireRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResponse<SceneListResponse> queryList(Long authLetterId, Integer pageNum, Integer pageSize) {
        Page<AuthLetterScene> page = new Page<>(pageNum, pageSize);
        Page<AuthLetterScene> result = sceneRepository.findPage(page, authLetterId);

        List<SceneListResponse> list = result.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());

        return PageResponse.of(list, result.getTotal(), pageNum, pageSize);
    }

    @Override
    public SceneDetailResponse getDetail(Long authLetterId, Long id) {
        AuthLetterScene scene = sceneRepository.findById(id);
        if (scene == null || !scene.getAuthLetterId().equals(authLetterId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Scene not found");
        }
        return convertToDetailResponse(scene);
    }

    @Override
    @Transactional
    public Long create(Long authLetterId, CreateSceneRequest request) {
        // Check scene name uniqueness
        if (sceneRepository.checkSceneNameExists(authLetterId, request.getSceneName(), null)) {
            throw new BusinessException(ErrorCode.SCENE_NAME_EXISTS,
                    "Scene name already exists: " + request.getSceneName());
        }

        // Validate rules or questionnaires
        boolean hasRules = request.getRules() != null && !request.getRules().isEmpty();
        boolean hasQuestionnaires = request.getQuestionnaires() != null && !request.getQuestionnaires().isEmpty();
        if (!hasRules && !hasQuestionnaires) {
            throw new BusinessException(ErrorCode.SCENE_RULE_QUESTIONNAIRE_REQUIRED,
                    "Rules and questionnaires must be configured at least one");
        }

        // Create scene
        AuthLetterScene scene = new AuthLetterScene();
        scene.setAuthLetterId(authLetterId);
        scene.setSceneName(request.getSceneName());
        scene.setIndustry(request.getIndustry());
        scene.setBusinessScene(request.getBusinessScene());
        scene.setDecisionLevel(request.getDecisionLevel());
        scene.setSpecificRule(request.getSpecificRule());

        Long sceneId = sceneRepository.insert(scene);

        // Create rules
        if (hasRules) {
            for (RuleGroupDTO ruleGroup : request.getRules()) {
                createRuleGroup(sceneId, ruleGroup);
            }
        }

        // Create questionnaires
        if (hasQuestionnaires) {
            for (SceneQuestionnaireDTO sqDTO : request.getQuestionnaires()) {
                createSceneQuestionnaire(sceneId, sqDTO);
            }
        }

        return sceneId;
    }

    @Override
    @Transactional
    public void update(Long authLetterId, Long id, CreateSceneRequest request) {
        AuthLetterScene scene = sceneRepository.findById(id);
        if (scene == null || !scene.getAuthLetterId().equals(authLetterId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Scene not found");
        }

        // Check scene name uniqueness
        if (sceneRepository.checkSceneNameExists(authLetterId, request.getSceneName(), id)) {
            throw new BusinessException(ErrorCode.SCENE_NAME_EXISTS,
                    "Scene name already exists: " + request.getSceneName());
        }

        // Validate rules or questionnaires
        boolean hasRules = request.getRules() != null && !request.getRules().isEmpty();
        boolean hasQuestionnaires = request.getQuestionnaires() != null && !request.getQuestionnaires().isEmpty();
        if (!hasRules && !hasQuestionnaires) {
            throw new BusinessException(ErrorCode.SCENE_RULE_QUESTIONNAIRE_REQUIRED,
                    "Rules and questionnaires must be configured at least one");
        }

        // Update scene
        scene.setSceneName(request.getSceneName());
        scene.setIndustry(request.getIndustry());
        scene.setBusinessScene(request.getBusinessScene());
        scene.setDecisionLevel(request.getDecisionLevel());
        scene.setSpecificRule(request.getSpecificRule());
        sceneRepository.update(scene);

        // Delete existing rules and questionnaires
        ruleRepository.deleteRulesBySceneId(id);
        sceneQuestionnaireRepository.deleteBySceneId(id);

        // Create new rules
        if (hasRules) {
            for (RuleGroupDTO ruleGroup : request.getRules()) {
                createRuleGroup(id, ruleGroup);
            }
        }

        // Create new questionnaires
        if (hasQuestionnaires) {
            for (SceneQuestionnaireDTO sqDTO : request.getQuestionnaires()) {
                createSceneQuestionnaire(id, sqDTO);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long authLetterId, Long id) {
        AuthLetterScene scene = sceneRepository.findById(id);
        if (scene == null || !scene.getAuthLetterId().equals(authLetterId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Scene not found");
        }

        // Delete rules and questionnaires first
        ruleRepository.deleteRulesBySceneId(id);
        sceneQuestionnaireRepository.deleteBySceneId(id);
        sceneRepository.delete(id);
    }

    @Override
    @Transactional
    public void batchDelete(Long authLetterId, List<Long> ids) {
        for (Long id : ids) {
            delete(authLetterId, id);
        }
    }

    @Override
    public List<SceneDetailResponse> getScenesByAuthLetterId(Long authLetterId) {
        List<AuthLetterScene> scenes = sceneRepository.findByAuthLetterId(authLetterId);
        return scenes.stream()
                .map(this::convertToDetailResponse)
                .collect(Collectors.toList());
    }

    private void createRuleGroup(Long sceneId, RuleGroupDTO ruleGroup) {
        AuthLetterRule rule = new AuthLetterRule();
        rule.setSceneId(sceneId);
        rule.setRuleName(ruleGroup.getRuleName());
        rule.setLogicType(ruleGroup.getLogicType());
        Long ruleId = ruleRepository.insertRule(rule);

        // Create conditions recursively
        if (ruleGroup.getConditions() != null) {
            for (RuleConditionDTO conditionDTO : ruleGroup.getConditions()) {
                createCondition(ruleId, null, conditionDTO);
            }
        }
    }

    private void createCondition(Long ruleId, Long parentConditionId, RuleConditionDTO conditionDTO) {
        AuthLetterRuleCondition condition = new AuthLetterRuleCondition();
        condition.setRuleId(ruleId);
        condition.setParentConditionId(parentConditionId);
        condition.setFieldCode(conditionDTO.getFieldCode());
        condition.setOperator(conditionDTO.getOperator());
        condition.setCompareType(conditionDTO.getCompareType());
        condition.setCompareFieldCode(conditionDTO.getCompareFieldCode());
        condition.setCompareValue(conditionDTO.getCompareValue());
        condition.setCompareUnit(conditionDTO.getCompareUnit());
        condition.setLogicType(conditionDTO.getLogicType());
        condition.setIsGroup(conditionDTO.getIsGroup() ? 1 : 0);
        condition.setGroupLogicType(conditionDTO.getGroupLogicType());
        condition.setSortOrder(conditionDTO.getSortOrder());

        Long conditionId = ruleRepository.insertCondition(condition);

        // Create children recursively
        if (Boolean.TRUE.equals(conditionDTO.getIsGroup()) && conditionDTO.getChildren() != null) {
            for (RuleConditionDTO childDTO : conditionDTO.getChildren()) {
                createCondition(ruleId, conditionId, childDTO);
            }
        }
    }

    private void createSceneQuestionnaire(Long sceneId, SceneQuestionnaireDTO sqDTO) {
        SceneQuestionnaire sq = new SceneQuestionnaire();
        sq.setSceneId(sceneId);
        sq.setQuestionId(sqDTO.getQuestionId());
        sq.setCorrectAnswerId(sqDTO.getCorrectAnswerId());
        sq.setSortOrder(sqDTO.getSortOrder());
        sceneQuestionnaireRepository.insert(sq);
    }

    private SceneListResponse convertToListResponse(AuthLetterScene scene) {
        SceneListResponse response = new SceneListResponse();
        response.setId(scene.getId());
        response.setSceneName(scene.getSceneName());
        response.setBusinessScene(scene.getBusinessScene());
        response.setBusinessSceneName(scene.getBusinessScene());
        response.setDecisionLevel(scene.getDecisionLevel());
        response.setDecisionLevelName(scene.getDecisionLevel());
        response.setSpecificRule(scene.getSpecificRule());
        response.setCreatedBy(scene.getCreatedBy());
        response.setCreatedTime(scene.getCreatedTime());
        response.setUpdatedBy(scene.getUpdatedBy());
        response.setUpdatedTime(scene.getUpdatedTime());

        // Parse industry JSON
        if (scene.getIndustry() != null) {
            try {
                List<String> industry = objectMapper.readValue(scene.getIndustry(), new TypeReference<List<String>>() {});
                response.setIndustry(industry);
                response.setIndustryName(String.join(",", industry));
            } catch (Exception e) {
                response.setIndustry(new ArrayList<>());
            }
        }

        return response;
    }

    private SceneDetailResponse convertToDetailResponse(AuthLetterScene scene) {
        SceneDetailResponse response = new SceneDetailResponse();
        response.setId(scene.getId());
        response.setSceneName(scene.getSceneName());
        response.setBusinessScene(scene.getBusinessScene());
        response.setDecisionLevel(scene.getDecisionLevel());
        response.setSpecificRule(scene.getSpecificRule());

        // Parse industry JSON
        if (scene.getIndustry() != null) {
            try {
                List<String> industry = objectMapper.readValue(scene.getIndustry(), new TypeReference<List<String>>() {});
                response.setIndustry(industry);
            } catch (Exception e) {
                response.setIndustry(new ArrayList<>());
            }
        }

        // Load rules
        List<AuthLetterRule> rules = ruleRepository.findRulesBySceneId(scene.getId());
        List<RuleGroupResponse> ruleResponses = new ArrayList<>();
        for (AuthLetterRule rule : rules) {
            ruleResponses.add(convertToRuleGroupResponse(rule));
        }
        response.setRules(ruleResponses);

        // Load questionnaires
        List<SceneQuestionnaire> sqs = sceneQuestionnaireRepository.findBySceneId(scene.getId());
        List<SceneQuestionnaireResponse> qResponses = new ArrayList<>();
        for (SceneQuestionnaire sq : sqs) {
            qResponses.add(convertToSceneQuestionnaireResponse(sq));
        }
        response.setQuestionnaires(qResponses);

        return response;
    }

    private RuleGroupResponse convertToRuleGroupResponse(AuthLetterRule rule) {
        RuleGroupResponse response = new RuleGroupResponse();
        response.setId(rule.getId());
        response.setRuleName(rule.getRuleName());
        response.setLogicType(rule.getLogicType());

        // Load conditions
        List<AuthLetterRuleCondition> conditions = ruleRepository.findConditionsByRuleId(rule.getId());
        List<RuleConditionResponse> conditionResponses = buildConditionTree(conditions, null);
        response.setConditions(conditionResponses);

        return response;
    }

    private List<RuleConditionResponse> buildConditionTree(List<AuthLetterRuleCondition> allConditions, Long parentConditionId) {
        List<RuleConditionResponse> result = new ArrayList<>();

        for (AuthLetterRuleCondition condition : allConditions) {
            boolean isMatch = (parentConditionId == null && condition.getParentConditionId() == null) ||
                    (parentConditionId != null && parentConditionId.equals(condition.getParentConditionId()));

            if (isMatch) {
                RuleConditionResponse response = convertToConditionResponse(condition);

                // Build children recursively
                if (condition.getIsGroup() == 1) {
                    List<RuleConditionResponse> children = buildConditionTree(allConditions, condition.getId());
                    response.setChildren(children);
                }

                result.add(response);
            }
        }

        return result;
    }

    private RuleConditionResponse convertToConditionResponse(AuthLetterRuleCondition condition) {
        RuleConditionResponse response = new RuleConditionResponse();
        response.setId(condition.getId());
        response.setFieldCode(condition.getFieldCode());
        response.setFieldName(condition.getFieldCode()); // Should lookup from rule param
        response.setOperator(condition.getOperator());
        response.setOperatorSymbol(getOperatorSymbol(condition.getOperator()));
        response.setCompareType(condition.getCompareType());
        response.setCompareTypeDesc(condition.getCompareType());
        response.setCompareFieldCode(condition.getCompareFieldCode());
        response.setCompareFieldName(condition.getCompareFieldCode());
        response.setCompareValue(condition.getCompareValue());
        response.setCompareUnit(condition.getCompareUnit());
        response.setCompareUnitName(condition.getCompareUnit());
        response.setLogicType(condition.getLogicType());
        response.setIsGroup(condition.getIsGroup() == 1);
        response.setGroupLogicType(condition.getGroupLogicType());
        response.setSortOrder(condition.getSortOrder());
        return response;
    }

    private SceneQuestionnaireResponse convertToSceneQuestionnaireResponse(SceneQuestionnaire sq) {
        SceneQuestionnaireResponse response = new SceneQuestionnaireResponse();
        response.setId(sq.getId());
        response.setQuestionId(sq.getQuestionId());
        response.setCorrectAnswerId(sq.getCorrectAnswerId());
        response.setSortOrder(sq.getSortOrder());

        // Load question info
        QuestionnaireQuestion question = questionnaireRepository.findQuestionById(sq.getQuestionId());
        if (question != null) {
            response.setQuestionCode(question.getQuestionCode());
            List<QuestionnaireQuestionText> texts = questionnaireRepository.findQuestionTexts(sq.getQuestionId());
            if (!texts.isEmpty()) {
                response.setQuestionText(texts.get(0).getQuestionText());
            }
        }

        // Load correct answer info
        if (sq.getCorrectAnswerId() != null) {
            QuestionnaireAnswer answer = questionnaireRepository.findAnswerById(sq.getCorrectAnswerId());
            if (answer != null) {
                response.setCorrectAnswerCode(answer.getAnswerCode());
                List<QuestionnaireAnswerText> answerTexts = questionnaireRepository.findAnswerTexts(sq.getCorrectAnswerId());
                if (!answerTexts.isEmpty()) {
                    response.setCorrectAnswerText(answerTexts.get(0).getAnswerText());
                }
            }
        }

        return response;
    }

    private String getOperatorSymbol(String operator) {
        switch (operator) {
            case "GT": return ">";
            case "LT": return "<";
            case "EQ": return "=";
            case "GE": return ">=";
            case "LE": return "<=";
            case "NE": return "!=";
            default: return operator;
        }
    }
}