package com.rathinam.toastmasters.modules.points.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.points.dto.CreatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.dto.PointRuleResponse;
import com.rathinam.toastmasters.modules.points.dto.UpdatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.service.PointRuleService;
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
@RequestMapping("/api/v1/point-rules")
public class PointRuleController {

    private final PointRuleService pointRuleService;

    public PointRuleController(PointRuleService pointRuleService) {
        this.pointRuleService = pointRuleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<PointRuleResponse>> createPointRule(@Valid @RequestBody CreatePointRuleRequest request) {
        PointRuleResponse response = pointRuleService.createPointRule(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Point rule created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PointRuleResponse>>> getAllPointRules(@RequestParam(required = false) Boolean activeOnly) {
        List<PointRuleResponse> response = pointRuleService.getAllPointRules(activeOnly);
        return ResponseEntity.ok(ApiResponse.success(response, "Point rules retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PointRuleResponse>> getPointRuleById(@PathVariable UUID id) {
        PointRuleResponse response = pointRuleService.getPointRuleById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Point rule retrieved successfully"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<PointRuleResponse>> updatePointRule(@PathVariable UUID id, @Valid @RequestBody UpdatePointRuleRequest request) {
        PointRuleResponse response = pointRuleService.updatePointRule(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Point rule updated successfully"));
    }
}
