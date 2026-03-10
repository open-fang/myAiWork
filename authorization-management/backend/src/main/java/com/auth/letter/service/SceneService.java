package com.auth.letter.service;

import com.auth.letter.dto.SceneDTO;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.Scene;
import com.auth.letter.exception.BusinessException;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SceneService {

    private final SceneRepository sceneRepository;
    private final AuthLetterRepository authLetterRepository;

    /**
     * Create a new scene under an authorization letter
     */
    @Transactional
    public SceneDTO create(Long authLetterId, SceneDTO dto) {
        AuthLetter authLetter = authLetterRepository.findById(authLetterId)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + authLetterId));

        Scene scene = new Scene();
        scene.setName(dto.getName());
        scene.setDescription(dto.getDescription());
        scene.setDecisionLevel(dto.getDecisionLevel());
        scene.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0);
        scene.setAuthLetter(authLetter);

        Scene saved = sceneRepository.save(scene);
        return toDTO(saved);
    }

    /**
     * Update a scene
     */
    @Transactional
    public SceneDTO update(Long id, SceneDTO dto) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scene not found: " + id));

        scene.setName(dto.getName());
        scene.setDescription(dto.getDescription());
        scene.setDecisionLevel(dto.getDecisionLevel());
        scene.setOrderIndex(dto.getOrderIndex());

        Scene saved = sceneRepository.save(scene);
        return toDTO(saved);
    }

    /**
     * Get scene by ID
     */
    public SceneDTO getById(Long id) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scene not found: " + id));
        return toDTO(scene);
    }

    /**
     * Get all scenes by authorization letter ID
     */
    public List<SceneDTO> getByAuthLetterId(Long authLetterId) {
        return sceneRepository.findByAuthLetterIdOrderByOrderIndexAsc(authLetterId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete a scene
     */
    @Transactional
    public void delete(Long id) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scene not found: " + id));

        sceneRepository.delete(scene);
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
        return dto;
    }
}