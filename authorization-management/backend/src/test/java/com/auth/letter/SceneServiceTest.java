package com.auth.letter;

import com.auth.letter.dto.SceneDTO;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.repository.SceneRepository;
import com.auth.letter.service.SceneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SceneServiceTest {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private AuthLetterRepository authLetterRepository;

    @Autowired
    private SceneRepository sceneRepository;

    private AuthLetter testAuthLetter;

    @BeforeEach
    void setUp() {
        testAuthLetter = new AuthLetter();
        testAuthLetter.setName("测试授权书");
        testAuthLetter.setStatus(AuthLetterStatus.DRAFT);
        testAuthLetter.setPublishLevel(OrgLevel.ORGANIZATION);
        testAuthLetter.setAuthorizedLevel(OrgLevel.REPRESENTATIVE_OFFICE);
        testAuthLetter.setValidFrom(LocalDateTime.now());
        testAuthLetter.setValidTo(LocalDateTime.now().plusDays(30));
        testAuthLetter = authLetterRepository.save(testAuthLetter);
    }

    @Test
    @DisplayName("创建场景 - 成功")
    void testCreateScene() {
        SceneDTO dto = new SceneDTO();
        dto.setName("审批场景1");
        dto.setDescription("金额小于100万的审批");
        dto.setDecisionLevel(1);
        dto.setOrderIndex(0);

        SceneDTO result = sceneService.create(testAuthLetter.getId(), dto);

        assertNotNull(result.getId());
        assertEquals("审批场景1", result.getName());
        assertEquals(1, result.getDecisionLevel());
        assertEquals(testAuthLetter.getId(), result.getAuthLetterId());
    }

    @Test
    @DisplayName("获取场景列表 - 成功")
    void testGetScenes() {
        SceneDTO dto1 = new SceneDTO();
        dto1.setName("场景1");
        dto1.setDecisionLevel(1);
        dto1.setOrderIndex(0);

        SceneDTO dto2 = new SceneDTO();
        dto2.setName("场景2");
        dto2.setDecisionLevel(2);
        dto2.setOrderIndex(1);

        sceneService.create(testAuthLetter.getId(), dto1);
        sceneService.create(testAuthLetter.getId(), dto2);

        List<SceneDTO> results = sceneService.getByAuthLetterId(testAuthLetter.getId());

        assertEquals(2, results.size());
        assertEquals("场景1", results.get(0).getName());
        assertEquals("场景2", results.get(1).getName());
    }

    @Test
    @DisplayName("更新场景 - 成功")
    void testUpdateScene() {
        SceneDTO dto = new SceneDTO();
        dto.setName("原场景");
        dto.setDecisionLevel(1);
        dto.setOrderIndex(0);

        SceneDTO created = sceneService.create(testAuthLetter.getId(), dto);

        SceneDTO updateDto = new SceneDTO();
        updateDto.setName("更新后的场景");
        updateDto.setDecisionLevel(2);
        updateDto.setDescription("更新后的描述");

        SceneDTO result = sceneService.update(created.getId(), updateDto);

        assertEquals("更新后的场景", result.getName());
        assertEquals(2, result.getDecisionLevel());
        assertEquals("更新后的描述", result.getDescription());
    }

    @Test
    @DisplayName("删除场景 - 成功")
    void testDeleteScene() {
        SceneDTO dto = new SceneDTO();
        dto.setName("待删除场景");
        dto.setDecisionLevel(1);

        SceneDTO created = sceneService.create(testAuthLetter.getId(), dto);
        sceneService.delete(created.getId());

        assertFalse(sceneRepository.findById(created.getId()).isPresent());
    }
}