package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import com.auth.letter.dto.ConditionDTO;
import com.auth.letter.dto.RuleDTO;
import com.auth.letter.service.RuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rule Controller
 */
@RestController
@RequestMapping("/api/scenes/{sceneId}/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    /**
     * Create a new rule
     */
    @PostMapping
    public ApiResponse<RuleDTO> create(@PathVariable Long sceneId, @Valid @RequestBody RuleDTO dto) {
        return ApiResponse.success("Created successfully", ruleService.create(sceneId, dto));
    }

    /**
     * Update a rule
     */
    @PutMapping("/{id}")
    public ApiResponse<RuleDTO> update(@PathVariable Long id, @Valid @RequestBody RuleDTO dto) {
        return ApiResponse.success("Updated successfully", ruleService.update(id, dto));
    }

    /**
     * Get rule by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<RuleDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(ruleService.getById(id));
    }

    /**
     * Get all rules by scene ID
     */
    @GetMapping
    public ApiResponse<List<RuleDTO>> getBySceneId(@PathVariable Long sceneId) {
        return ApiResponse.success(ruleService.getBySceneId(sceneId));
    }

    /**
     * Delete a rule
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        ruleService.delete(id);
        return ApiResponse.success("Deleted successfully", null);
    }

    /**
     * Add condition to a rule
     */
    @PostMapping("/{ruleId}/conditions")
    public ApiResponse<ConditionDTO> addCondition(@PathVariable Long ruleId, @RequestBody ConditionDTO dto) {
        return ApiResponse.success("Condition added successfully", ruleService.addCondition(ruleId, dto));
    }

    /**
     * Add nested condition
     */
    @PostMapping("/conditions/{parentConditionId}/children")
    public ApiResponse<ConditionDTO> addNestedCondition(
            @PathVariable Long parentConditionId,
            @RequestBody ConditionDTO dto) {
        return ApiResponse.success("Nested condition added successfully",
                ruleService.addNestedCondition(parentConditionId, dto));
    }

    /**
     * Delete a condition
     */
    @DeleteMapping("/conditions/{conditionId}")
    public ApiResponse<Void> deleteCondition(@PathVariable Long conditionId) {
        ruleService.deleteCondition(conditionId);
        return ApiResponse.success("Condition deleted successfully", null);
    }
}