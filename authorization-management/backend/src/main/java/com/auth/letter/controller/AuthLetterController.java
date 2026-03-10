package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import com.auth.letter.dto.AuthLetterDTO;
import com.auth.letter.dto.MatchRequest;
import com.auth.letter.dto.MatchResult;
import com.auth.letter.service.AuthLetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authorization Letter Controller
 */
@RestController
@RequestMapping("/api/auth-letters")
@RequiredArgsConstructor
public class AuthLetterController {

    private final AuthLetterService authLetterService;

    /**
     * Create a new authorization letter
     */
    @PostMapping
    public ApiResponse<AuthLetterDTO> create(@Valid @RequestBody AuthLetterDTO dto) {
        return ApiResponse.success("Created successfully", authLetterService.create(dto));
    }

    /**
     * Update an authorization letter
     */
    @PutMapping("/{id}")
    public ApiResponse<AuthLetterDTO> update(@PathVariable Long id, @Valid @RequestBody AuthLetterDTO dto) {
        return ApiResponse.success("Updated successfully", authLetterService.update(id, dto));
    }

    /**
     * Get authorization letter by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<AuthLetterDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(authLetterService.getById(id));
    }

    /**
     * Get all authorization letters
     */
    @GetMapping
    public ApiResponse<List<AuthLetterDTO>> getAll() {
        return ApiResponse.success(authLetterService.getAll());
    }

    /**
     * Delete an authorization letter
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authLetterService.delete(id);
        return ApiResponse.success("Deleted successfully", null);
    }

    /**
     * Publish an authorization letter
     */
    @PostMapping("/{id}/publish")
    public ApiResponse<AuthLetterDTO> publish(@PathVariable Long id) {
        return ApiResponse.success("Published successfully", authLetterService.publish(id));
    }

    /**
     * Match scenes based on request (for machine-to-machine API)
     */
    @PostMapping("/match")
    public ApiResponse<MatchResult> match(@RequestBody MatchRequest request) {
        return ApiResponse.success(authLetterService.match(request));
    }

    /**
     * Expire letters (scheduled task endpoint)
     */
    @PostMapping("/expire")
    public ApiResponse<Void> expireLetters() {
        authLetterService.expireLetters();
        return ApiResponse.success("Letters expired successfully", null);
    }
}