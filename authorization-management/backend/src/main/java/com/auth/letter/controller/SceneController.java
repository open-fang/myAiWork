package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import com.auth.letter.dto.SceneDTO;
import com.auth.letter.service.SceneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Scene Controller
 */
@RestController
@RequestMapping("/api/auth-letters/{authLetterId}/scenes")
@RequiredArgsConstructor
public class SceneController {

    private final SceneService sceneService;

    /**
     * Create a new scene
     */
    @PostMapping
    public ApiResponse<SceneDTO> create(@PathVariable Long authLetterId, @Valid @RequestBody SceneDTO dto) {
        return ApiResponse.success("Created successfully", sceneService.create(authLetterId, dto));
    }

    /**
     * Update a scene
     */
    @PutMapping("/{id}")
    public ApiResponse<SceneDTO> update(@PathVariable Long id, @Valid @RequestBody SceneDTO dto) {
        return ApiResponse.success("Updated successfully", sceneService.update(id, dto));
    }

    /**
     * Get scene by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<SceneDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(sceneService.getById(id));
    }

    /**
     * Get all scenes by authorization letter ID
     */
    @GetMapping
    public ApiResponse<List<SceneDTO>> getByAuthLetterId(@PathVariable Long authLetterId) {
        return ApiResponse.success(sceneService.getByAuthLetterId(authLetterId));
    }

    /**
     * Delete a scene
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sceneService.delete(id);
        return ApiResponse.success("Deleted successfully", null);
    }
}