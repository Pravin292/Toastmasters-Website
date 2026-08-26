package com.rathinam.toastmasters.modules.meeting.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.meeting.dto.CreateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingResponse;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingWorkflowResponse;
import com.rathinam.toastmasters.modules.meeting.dto.UpdateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MeetingResponse>> createMeeting(@Valid @RequestBody CreateMeetingRequest request) {
        MeetingResponse response = meetingService.createMeeting(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Meeting created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingResponse>> getMeetingById(@PathVariable UUID id) {
        MeetingResponse response = meetingService.getMeetingById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MeetingResponse>>> getMeetings(@PageableDefault(size = 10, sort = "meetingNumber") Pageable pageable) {
        Page<MeetingResponse> responsePage = meetingService.getMeetings(pageable);
        return ResponseEntity.ok(ApiResponse.success(responsePage, "Meetings retrieved successfully"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingResponse>> updateMeeting(@PathVariable UUID id, @Valid @RequestBody UpdateMeetingRequest request) {
        MeetingResponse response = meetingService.updateMeeting(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting updated successfully"));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<MeetingResponse>> startMeeting(@PathVariable UUID id) {
        MeetingResponse response = meetingService.startMeeting(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting started successfully"));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<MeetingResponse>> completeMeeting(@PathVariable UUID id) {
        MeetingResponse response = meetingService.completeMeeting(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting completed successfully"));
    }

    @GetMapping("/{id}/workflow")
    public ResponseEntity<ApiResponse<MeetingWorkflowResponse>> getMeetingWorkflow(@PathVariable UUID id) {
        MeetingWorkflowResponse response = meetingService.getMeetingWorkflow(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting workflow details retrieved successfully"));
    }
}
