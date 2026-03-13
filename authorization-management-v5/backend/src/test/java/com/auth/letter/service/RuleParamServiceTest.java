package com.auth.letter.service;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthRuleParam;
import com.auth.letter.enums.RuleParamStatus;
import com.auth.letter.repository.AuthRuleParamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RuleParamService Test
 */
@SpringBootTest
@ActiveProfiles("test")
class RuleParamServiceTest {

    @Autowired
    private RuleParamService ruleParamService;

    @Autowired
    private AuthRuleParamRepository ruleParamRepository;

    @BeforeEach
    void setUp() {
        ruleParamRepository.deleteAll();
    }

    @Test
    void testCreateRuleParam() {
        RuleParamDTO dto = new RuleParamDTO();
        dto.setName("金额");
        dto.setNameEn("amount");
        dto.setDataType("NUMBER");
        dto.setStatus(RuleParamStatus.ACTIVE.getCode());

        Long id = ruleParamService.create(dto);

        assertNotNull(id);
        assertTrue(id > 0);

        RuleParamDTO saved = ruleParamService.getById(id);
        assertEquals("金额", saved.getName());
        assertEquals("amount", saved.getNameEn());
        assertEquals("NUMBER", saved.getDataType());
    }

    @Test
    void testCreateWithDuplicateNameEn() {
        // Create first
        RuleParamDTO dto1 = new RuleParamDTO();
        dto1.setName("金额");
        dto1.setNameEn("amount");
        dto1.setDataType("NUMBER");
        ruleParamService.create(dto1);

        // Try to create with same nameEn
        RuleParamDTO dto2 = new RuleParamDTO();
        dto2.setName("金额2");
        dto2.setNameEn("amount");
        dto2.setDataType("TEXT");

        assertThrows(RuntimeException.class, () -> {
            ruleParamService.create(dto2);
        });
    }

    @Test
    void testUpdateRuleParam() {
        // Create
        RuleParamDTO dto = new RuleParamDTO();
        dto.setName("原始名称");
        dto.setNameEn("original_name");
        dto.setDataType("TEXT");
        Long id = ruleParamService.create(dto);

        // Update
        RuleParamDTO updateDto = new RuleParamDTO();
        updateDto.setName("更新名称");
        updateDto.setNameEn("updated_name");
        updateDto.setDataType("NUMBER");
        updateDto.setStatus(RuleParamStatus.ACTIVE.getCode());

        ruleParamService.update(id, updateDto);

        RuleParamDTO updated = ruleParamService.getById(id);
        assertEquals("更新名称", updated.getName());
        assertEquals("updated_name", updated.getNameEn());
        assertEquals("NUMBER", updated.getDataType());
    }

    @Test
    void testDeleteRuleParam() {
        RuleParamDTO dto = new RuleParamDTO();
        dto.setName("待删除");
        dto.setNameEn("to_delete");
        dto.setDataType("TEXT");
        Long id = ruleParamService.create(dto);

        ruleParamService.delete(id);

        assertThrows(RuntimeException.class, () -> {
            ruleParamService.getById(id);
        });
    }

    @Test
    void testQueryList() {
        // Create multiple
        for (int i = 1; i <= 3; i++) {
            RuleParamDTO dto = new RuleParamDTO();
            dto.setName("参数" + i);
            dto.setNameEn("param_" + i);
            dto.setDataType("TEXT");
            ruleParamService.create(dto);
        }

        RuleParamQueryDTO queryDTO = new RuleParamQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        PageResult<RuleParamListVO> result = ruleParamService.queryList(queryDTO);

        assertEquals(3, result.getTotal());
        assertEquals(3, result.getList().size());
    }

    @Test
    void testQueryListWithFilter() {
        // Create with different status
        RuleParamDTO dto1 = new RuleParamDTO();
        dto1.setName("生效参数");
        dto1.setNameEn("active_param");
        dto1.setDataType("TEXT");
        dto1.setStatus(RuleParamStatus.ACTIVE.getCode());
        ruleParamService.create(dto1);

        RuleParamDTO dto2 = new RuleParamDTO();
        dto2.setName("失效参数");
        dto2.setNameEn("inactive_param");
        dto2.setDataType("NUMBER");
        dto2.setStatus(RuleParamStatus.INACTIVE.getCode());
        ruleParamService.create(dto2);

        // Query active only
        RuleParamQueryDTO queryDTO = new RuleParamQueryDTO();
        queryDTO.setStatus(RuleParamStatus.ACTIVE.getCode());
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        PageResult<RuleParamListVO> result = ruleParamService.queryList(queryDTO);

        assertEquals(1, result.getTotal());
        assertEquals("生效参数", result.getList().get(0).getName());
    }

    @Test
    void testBatchActivate() {
        // Create inactive params
        List<Long> ids = Arrays.asList(
                createRuleParam("参数1", "param_1", RuleParamStatus.INACTIVE.getCode()),
                createRuleParam("参数2", "param_2", RuleParamStatus.INACTIVE.getCode())
        );

        int count = ruleParamService.batchActivate(ids);

        assertEquals(2, count);

        // Verify all are active
        for (Long id : ids) {
            RuleParamDTO dto = ruleParamService.getById(id);
            assertEquals(RuleParamStatus.ACTIVE.getCode(), dto.getStatus());
        }
    }

    @Test
    void testBatchDeactivate() {
        List<Long> ids = Arrays.asList(
                createRuleParam("参数1", "param_1", RuleParamStatus.ACTIVE.getCode()),
                createRuleParam("参数2", "param_2", RuleParamStatus.ACTIVE.getCode())
        );

        int count = ruleParamService.batchDeactivate(ids);

        assertEquals(2, count);
    }

    @Test
    void testGetAllActive() {
        // Create mix of active and inactive
        createRuleParam("生效1", "active_1", RuleParamStatus.ACTIVE.getCode());
        createRuleParam("生效2", "active_2", RuleParamStatus.ACTIVE.getCode());
        createRuleParam("失效1", "inactive_1", RuleParamStatus.INACTIVE.getCode());

        List<RuleParamListVO> result = ruleParamService.getAllActive();

        assertEquals(2, result.size());
    }

    private Long createRuleParam(String name, String nameEn, String status) {
        RuleParamDTO dto = new RuleParamDTO();
        dto.setName(name);
        dto.setNameEn(nameEn);
        dto.setDataType("TEXT");
        dto.setStatus(status);
        return ruleParamService.create(dto);
    }
}