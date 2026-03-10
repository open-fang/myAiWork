package com.auth.letter;

import com.auth.letter.dto.ConditionDTO;
import com.auth.letter.dto.RuleDTO;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.repository.RuleRepository;
import com.auth.letter.repository.SceneRepository;
import com.auth.letter.service.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private AuthLetterRepository authLetterRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private RuleRepository ruleRepository;

    private Scene testScene;

    @BeforeEach
    void setUp() {
        AuthLetter authLetter = new AuthLetter();
        authLetter.setName("测试授权书");
        authLetter.setStatus(AuthLetterStatus.DRAFT);
        authLetter.setPublishLevel(OrgLevel.ORGANIZATION);
        authLetter.setAuthorizedLevel(OrgLevel.REPRESENTATIVE_OFFICE);
        authLetter.setValidFrom(LocalDateTime.now());
        authLetter.setValidTo(LocalDateTime.now().plusDays(30));
        authLetter = authLetterRepository.save(authLetter);

        testScene = new Scene();
        testScene.setName("测试场景");
        testScene.setDecisionLevel(1);
        testScene.setAuthLetter(authLetter);
        testScene = sceneRepository.save(testScene);
    }

    @Test
    @DisplayName("创建规则 - 成功")
    void testCreateRule() {
        RuleDTO dto = new RuleDTO();
        dto.setName("金额规则");
        dto.setDescription("金额小于100万");
        dto.setOrderIndex(0);

        RuleDTO result = ruleService.create(testScene.getId(), dto);

        assertNotNull(result.getId());
        assertEquals("金额规则", result.getName());
        assertEquals("金额小于100万", result.getDescription());
        assertEquals(testScene.getId(), result.getSceneId());
    }

    @Test
    @DisplayName("创建规则带条件 - 成功")
    void testCreateRuleWithConditions() {
        RuleDTO dto = new RuleDTO();
        dto.setName("条件规则");
        dto.setOrderIndex(0);

        List<ConditionDTO> conditions = new ArrayList<>();
        ConditionDTO condition = new ConditionDTO();
        condition.setFieldName("amount");
        condition.setOperator("LT");
        condition.setValue("1000000");
        conditions.add(condition);
        dto.setConditions(conditions);

        RuleDTO result = ruleService.create(testScene.getId(), dto);

        assertNotNull(result.getId());
        assertNotNull(result.getConditions());
        assertEquals(1, result.getConditions().size());
        assertEquals("amount", result.getConditions().get(0).getFieldName());
        assertEquals("LT", result.getConditions().get(0).getOperator());
        assertEquals("1000000", result.getConditions().get(0).getValue());
    }

    @Test
    @DisplayName("获取规则列表 - 成功")
    void testGetRules() {
        RuleDTO dto1 = new RuleDTO();
        dto1.setName("规则1");
        dto1.setOrderIndex(0);

        RuleDTO dto2 = new RuleDTO();
        dto2.setName("规则2");
        dto2.setOrderIndex(1);

        ruleService.create(testScene.getId(), dto1);
        ruleService.create(testScene.getId(), dto2);

        List<RuleDTO> results = ruleService.getBySceneId(testScene.getId());

        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("更新规则 - 成功")
    void testUpdateRule() {
        RuleDTO dto = new RuleDTO();
        dto.setName("原规则");
        dto.setOrderIndex(0);

        RuleDTO created = ruleService.create(testScene.getId(), dto);

        RuleDTO updateDto = new RuleDTO();
        updateDto.setName("更新后的规则");
        updateDto.setDescription("更新后的描述");

        RuleDTO result = ruleService.update(created.getId(), updateDto);

        assertEquals("更新后的规则", result.getName());
        assertEquals("更新后的描述", result.getDescription());
    }

    @Test
    @DisplayName("删除规则 - 成功")
    void testDeleteRule() {
        RuleDTO dto = new RuleDTO();
        dto.setName("待删除规则");

        RuleDTO created = ruleService.create(testScene.getId(), dto);
        ruleService.delete(testScene.getId(), created.getId());

        assertFalse(ruleRepository.findById(created.getId()).isPresent());
    }

    @Test
    @DisplayName("添加条件 - 成功")
    void testAddCondition() {
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("测试规则");
        RuleDTO createdRule = ruleService.create(testScene.getId(), ruleDto);

        ConditionDTO conditionDto = new ConditionDTO();
        conditionDto.setFieldName("status");
        conditionDto.setOperator("EQ");
        conditionDto.setValue("approved");

        ConditionDTO result = ruleService.addCondition(createdRule.getId(), conditionDto);

        assertNotNull(result.getId());
        assertEquals("status", result.getFieldName());
        assertEquals("EQ", result.getOperator());
        assertEquals("approved", result.getValue());
    }
}