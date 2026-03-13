package com.auth.letter.service;

import com.auth.letter.dto.*;
import com.auth.letter.entity.*;
import com.auth.letter.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SceneMatchService Test
 */
@SpringBootTest
@ActiveProfiles("test")
class SceneMatchServiceTest {

    @Autowired
    private SceneMatchService sceneMatchService;

    @Autowired
    private AuthLetterRepository authLetterRepository;

    @Autowired
    private AuthSceneRepository sceneRepository;

    @Autowired
    private AuthLookupRepository lookupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long authLetterId;

    @BeforeEach
    void setUp() {
        sceneRepository.deleteAll();
        authLetterRepository.deleteAll();
        lookupRepository.deleteAll();

        // Create lookup data
        createLookup("DECISION_LEVEL", "DEC_LEVEL_1", "一级决策");
        createLookup("DECISION_LEVEL", "DEC_LEVEL_2", "二级决策");

        // Create auth letter
        AuthLetter authLetter = new AuthLetter();
        authLetter.setCode("AUTH001");
        authLetter.setName("测试授权书");
        authLetter.setStatus("PUBLISHED");
        authLetter.setCreatedBy("test");
        authLetter = authLetterRepository.save(authLetter);
        authLetterId = authLetter.getId();
    }

    private void createLookup(String type, String code, String name) {
        AuthLookup lookup = new AuthLookup();
        lookup.setLookupType(type);
        lookup.setLookupCode(code);
        lookup.setLookupName(name);
        lookup.setStatus("ACTIVE");
        lookupRepository.save(lookup);
    }

    @Test
    void testMatchNoScenes() {
        SceneMatchRequestDTO request = new SceneMatchRequestDTO();
        request.setAuthLetterId(authLetterId);
        request.setParams(new HashMap<>());

        List<SceneMatchResultVO> results = sceneMatchService.matchScenes(request);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testMatchSceneWithNoConditions() throws Exception {
        // Create scene without conditions
        AuthScene scene = new AuthScene();
        scene.setAuthLetterId(authLetterId);
        scene.setName("无条件场景");
        scene.setDecisionLevel("DEC_LEVEL_1");
        scene.setCreatedBy("test");
        sceneRepository.save(scene);

        SceneMatchRequestDTO request = new SceneMatchRequestDTO();
        request.setAuthLetterId(authLetterId);
        request.setParams(new HashMap<>());

        List<SceneMatchResultVO> results = sceneMatchService.matchScenes(request);

        assertEquals(1, results.size());
        assertEquals("无条件场景", results.get(0).getSceneName());
        assertEquals("DEC_LEVEL_1", results.get(0).getDecisionLevel());
    }

    @Test
    void testMatchSceneWithSimpleCondition() throws Exception {
        // Create scene with condition: amount > 1000
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("logicOperator", "AND");

        List<Map<String, Object>> conditions = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("fieldName", "amount");
        condition.put("operator", "OP_GT");

        Map<String, Object> compareConfig = new HashMap<>();
        compareConfig.put("type", "NUMBER");
        compareConfig.put("value", 1000);
        condition.put("compareConfig", compareConfig);

        conditions.add(condition);
        conditionMap.put("conditions", conditions);

        String conditionJson = objectMapper.writeValueAsString(conditionMap);

        AuthScene scene = new AuthScene();
        scene.setAuthLetterId(authLetterId);
        scene.setName("金额大于1000场景");
        scene.setDecisionLevel("DEC_LEVEL_2");
        scene.setConditionGroups(conditionJson);
        scene.setCreatedBy("test");
        sceneRepository.save(scene);

        // Test matching case
        SceneMatchRequestDTO request1 = new SceneMatchRequestDTO();
        request1.setAuthLetterId(authLetterId);
        Map<String, Object> params1 = new HashMap<>();
        params1.put("amount", 1500);
        request1.setParams(params1);

        List<SceneMatchResultVO> results1 = sceneMatchService.matchScenes(request1);
        assertEquals(1, results1.size());

        // Test non-matching case
        SceneMatchRequestDTO request2 = new SceneMatchRequestDTO();
        request2.setAuthLetterId(authLetterId);
        Map<String, Object> params2 = new HashMap<>();
        params2.put("amount", 500);
        request2.setParams(params2);

        List<SceneMatchResultVO> results2 = sceneMatchService.matchScenes(request2);
        assertTrue(results2.isEmpty());
    }

    @Test
    void testMatchMultipleScenes() throws Exception {
        // Create two scenes
        AuthScene scene1 = new AuthScene();
        scene1.setAuthLetterId(authLetterId);
        scene1.setName("场景1");
        scene1.setDecisionLevel("DEC_LEVEL_1");
        scene1.setCreatedBy("test");
        sceneRepository.save(scene1);

        AuthScene scene2 = new AuthScene();
        scene2.setAuthLetterId(authLetterId);
        scene2.setName("场景2");
        scene2.setDecisionLevel("DEC_LEVEL_2");
        scene2.setCreatedBy("test");
        sceneRepository.save(scene2);

        SceneMatchRequestDTO request = new SceneMatchRequestDTO();
        request.setAuthLetterId(authLetterId);
        request.setParams(new HashMap<>());

        List<SceneMatchResultVO> results = sceneMatchService.matchScenes(request);

        assertEquals(2, results.size());
    }

    @Test
    void testMatchWithOrCondition() throws Exception {
        // Create scene with OR condition: region = "A" OR region = "B"
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("logicOperator", "OR");

        List<Map<String, Object>> conditions = new ArrayList<>();

        Map<String, Object> condition1 = new HashMap<>();
        condition1.put("fieldName", "region");
        condition1.put("operator", "OP_EQ");
        Map<String, Object> config1 = new HashMap<>();
        config1.put("type", "TEXT");
        config1.put("value", "A");
        condition1.put("compareConfig", config1);
        conditions.add(condition1);

        Map<String, Object> condition2 = new HashMap<>();
        condition2.put("fieldName", "region");
        condition2.put("operator", "OP_EQ");
        Map<String, Object> config2 = new HashMap<>();
        config2.put("type", "TEXT");
        config2.put("value", "B");
        condition2.put("compareConfig", config2);
        conditions.add(condition2);

        conditionMap.put("conditions", conditions);

        String conditionJson = objectMapper.writeValueAsString(conditionMap);

        AuthScene scene = new AuthScene();
        scene.setAuthLetterId(authLetterId);
        scene.setName("区域A或B场景");
        scene.setDecisionLevel("DEC_LEVEL_1");
        scene.setConditionGroups(conditionJson);
        scene.setCreatedBy("test");
        sceneRepository.save(scene);

        // Test matching region A
        SceneMatchRequestDTO request1 = new SceneMatchRequestDTO();
        request1.setAuthLetterId(authLetterId);
        Map<String, Object> params1 = new HashMap<>();
        params1.put("region", "A");
        request1.setParams(params1);

        List<SceneMatchResultVO> results1 = sceneMatchService.matchScenes(request1);
        assertEquals(1, results1.size());

        // Test matching region B
        SceneMatchRequestDTO request2 = new SceneMatchRequestDTO();
        request2.setAuthLetterId(authLetterId);
        Map<String, Object> params2 = new HashMap<>();
        params2.put("region", "B");
        request2.setParams(params2);

        List<SceneMatchResultVO> results2 = sceneMatchService.matchScenes(request2);
        assertEquals(1, results2.size());

        // Test non-matching region C
        SceneMatchRequestDTO request3 = new SceneMatchRequestDTO();
        request3.setAuthLetterId(authLetterId);
        Map<String, Object> params3 = new HashMap<>();
        params3.put("region", "C");
        request3.setParams(params3);

        List<SceneMatchResultVO> results3 = sceneMatchService.matchScenes(request3);
        assertTrue(results3.isEmpty());
    }

    @Test
    void testMatchWithNestedConditionGroup() throws Exception {
        // Create scene with nested condition:
        // amount > 1000 AND (region = "A" OR region = "B")
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("logicOperator", "AND");

        List<Map<String, Object>> conditions = new ArrayList<>();

        // First condition: amount > 1000
        Map<String, Object> amountCondition = new HashMap<>();
        amountCondition.put("fieldName", "amount");
        amountCondition.put("operator", "OP_GT");
        Map<String, Object> amountConfig = new HashMap<>();
        amountConfig.put("type", "NUMBER");
        amountConfig.put("value", 1000);
        amountCondition.put("compareConfig", amountConfig);
        conditions.add(amountCondition);

        // Nested condition group: region = "A" OR region = "B"
        Map<String, Object> nestedGroup = new HashMap<>();
        nestedGroup.put("logicOperator", "OR");

        List<Map<String, Object>> nestedConditions = new ArrayList<>();

        Map<String, Object> regionA = new HashMap<>();
        regionA.put("fieldName", "region");
        regionA.put("operator", "OP_EQ");
        Map<String, Object> configA = new HashMap<>();
        configA.put("type", "TEXT");
        configA.put("value", "A");
        regionA.put("compareConfig", configA);
        nestedConditions.add(regionA);

        Map<String, Object> regionB = new HashMap<>();
        regionB.put("fieldName", "region");
        regionB.put("operator", "OP_EQ");
        Map<String, Object> configB = new HashMap<>();
        configB.put("type", "TEXT");
        configB.put("value", "B");
        regionB.put("compareConfig", configB);
        nestedConditions.add(regionB);

        nestedGroup.put("conditions", nestedConditions);
        conditions.add(nestedGroup);

        conditionMap.put("conditions", conditions);

        String conditionJson = objectMapper.writeValueAsString(conditionMap);

        AuthScene scene = new AuthScene();
        scene.setAuthLetterId(authLetterId);
        scene.setName("嵌套条件场景");
        scene.setDecisionLevel("DEC_LEVEL_1");
        scene.setConditionGroups(conditionJson);
        scene.setCreatedBy("test");
        sceneRepository.save(scene);

        // Test matching: amount=2000, region=A
        SceneMatchRequestDTO request1 = new SceneMatchRequestDTO();
        request1.setAuthLetterId(authLetterId);
        Map<String, Object> params1 = new HashMap<>();
        params1.put("amount", 2000);
        params1.put("region", "A");
        request1.setParams(params1);

        List<SceneMatchResultVO> results1 = sceneMatchService.matchScenes(request1);
        assertEquals(1, results1.size());

        // Test non-matching: amount=500, region=A (amount too low)
        SceneMatchRequestDTO request2 = new SceneMatchRequestDTO();
        request2.setAuthLetterId(authLetterId);
        Map<String, Object> params2 = new HashMap<>();
        params2.put("amount", 500);
        params2.put("region", "A");
        request2.setParams(params2);

        List<SceneMatchResultVO> results2 = sceneMatchService.matchScenes(request2);
        assertTrue(results2.isEmpty());

        // Test non-matching: amount=2000, region=C (wrong region)
        SceneMatchRequestDTO request3 = new SceneMatchRequestDTO();
        request3.setAuthLetterId(authLetterId);
        Map<String, Object> params3 = new HashMap<>();
        params3.put("amount", 2000);
        params3.put("region", "C");
        request3.setParams(params3);

        List<SceneMatchResultVO> results3 = sceneMatchService.matchScenes(request3);
        assertTrue(results3.isEmpty());
    }
}