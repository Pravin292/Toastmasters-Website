package com.rathinam.toastmasters.modules.achievement.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.achievement.dto.AchievementDefinitionResponse;
import com.rathinam.toastmasters.modules.achievement.dto.CreateAchievementDefinitionRequest;
import com.rathinam.toastmasters.modules.achievement.dto.UpdateAchievementDefinitionRequest;
import com.rathinam.toastmasters.modules.achievement.service.AchievementDefinitionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementDefinitionController {

    private final AchievementDefinitionService definitionService;

    public AchievementDefinitionController(AchievementDefinitionService definitionService) {
        this.definitionService = definitionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<AchievementDefinitionResponse>> createAchievementDefinition(
            @Valid @RequestBody CreateAchievementDefinitionRequest request) {
        AchievementDefinitionResponse response = definitionService.createAchievementDefinition(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Achievement definition created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AchievementDefinitionResponse>>> getAllAchievementDefinitions(
            @RequestParam(required = false) Boolean activeOnly) {
        List<AchievementDefinitionResponse> response = definitionService.getAllAchievementDefinitions(activeOnly);
        return ResponseEntity.ok(ApiResponse.success(response, "Achievement definitions retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AchievementDefinitionResponse>> getAchievementDefinitionById(
            @PathVariable UUID id) {
        AchievementDefinitionResponse response = definitionService.getAchievementDefinitionById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Achievement definition retrieved successfully"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<AchievementDefinitionResponse>> updateAchievementDefinition(
            @PathVariable UUID id,
            @RequestBody UpdateAchievementDefinitionRequest request) {
        AchievementDefinitionResponse response = definitionService.updateAchievementDefinition(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Achievement definition updated successfully"));
    }
}
