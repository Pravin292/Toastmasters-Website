package com.rathinam.toastmasters.modules.meetingrole.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleAssignmentRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleAssignmentResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.UpdateRoleAssignmentRequest;
import com.rathinam.toastmasters.modules.meetingrole.service.MeetingRoleAssignmentService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MeetingRoleAssignmentController {

    private final MeetingRoleAssignmentService assignmentService;

    public MeetingRoleAssignmentController(MeetingRoleAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/meetings/{meetingId}/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<RoleAssignmentResponse>> assignRole(
            @PathVariable UUID meetingId,
            @Valid @RequestBody CreateRoleAssignmentRequest request) {
        RoleAssignmentResponse response = assignmentService.assignRole(meetingId, request);
        return new ResponseEntity<>(ApiResponse.success(response, "Role assigned successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/meetings/{meetingId}/roles")
    public ResponseEntity<ApiResponse<List<RoleAssignmentResponse>>> getMeetingRoleAssignments(@PathVariable UUID meetingId) {
        List<RoleAssignmentResponse> response = assignmentService.getMeetingRoleAssignments(meetingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting role assignments retrieved successfully"));
    }

    @GetMapping("/meeting-roles/{assignmentId}")
    public ResponseEntity<ApiResponse<RoleAssignmentResponse>> getAssignmentById(@PathVariable UUID assignmentId) {
        RoleAssignmentResponse response = assignmentService.getAssignmentById(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Role assignment retrieved successfully"));
    }

    @PatchMapping("/meeting-roles/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<RoleAssignmentResponse>> updateAssignment(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody UpdateRoleAssignmentRequest request) {
        RoleAssignmentResponse response = assignmentService.updateAssignment(assignmentId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Role assignment updated successfully"));
    }
}
