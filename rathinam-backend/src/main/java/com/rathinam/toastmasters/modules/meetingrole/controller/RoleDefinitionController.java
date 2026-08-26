package com.rathinam.toastmasters.modules.meetingrole.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleDefinitionResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.UpdateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.service.RoleDefinitionService;
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
@RequestMapping("/api/v1/roles")
public class RoleDefinitionController {

    private final RoleDefinitionService roleDefinitionService;

    public RoleDefinitionController(RoleDefinitionService roleDefinitionService) {
        this.roleDefinitionService = roleDefinitionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<RoleDefinitionResponse>> createRoleDefinition(@Valid @RequestBody CreateRoleDefinitionRequest request) {
        RoleDefinitionResponse response = roleDefinitionService.createRoleDefinition(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Role definition created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDefinitionResponse>>> getAllRoleDefinitions(@RequestParam(required = false) Boolean activeOnly) {
        List<RoleDefinitionResponse> response = roleDefinitionService.getAllRoleDefinitions(activeOnly);
        return ResponseEntity.ok(ApiResponse.success(response, "Role definitions retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDefinitionResponse>> getRoleDefinitionById(@PathVariable UUID id) {
        RoleDefinitionResponse response = roleDefinitionService.getRoleDefinitionById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Role definition retrieved successfully"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<RoleDefinitionResponse>> updateRoleDefinition(@PathVariable UUID id, @Valid @RequestBody UpdateRoleDefinitionRequest request) {
        RoleDefinitionResponse response = roleDefinitionService.updateRoleDefinition(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Role definition updated successfully"));
    }
}
