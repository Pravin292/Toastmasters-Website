package com.rathinam.toastmasters.modules.points.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.points.dto.LeaderboardEntryResponse;
import com.rathinam.toastmasters.modules.points.dto.ManualPointAdjustmentRequest;
import com.rathinam.toastmasters.modules.points.dto.MeetingPointsSummaryResponse;
import com.rathinam.toastmasters.modules.points.dto.MemberPointsSummaryResponse;
import com.rathinam.toastmasters.modules.points.dto.PointEventResponse;
import com.rathinam.toastmasters.modules.points.service.PointAwardService;
import com.rathinam.toastmasters.modules.points.service.PointEventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PointEventController {

    private final PointAwardService pointAwardService;
    private final PointEventService pointEventService;

    public PointEventController(PointAwardService pointAwardService, PointEventService pointEventService) {
        this.pointAwardService = pointAwardService;
        this.pointEventService = pointEventService;
    }

    @PostMapping("/points/manual")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<PointEventResponse>> awardManualPoints(@Valid @RequestBody ManualPointAdjustmentRequest request) {
        PointEventResponse response = pointAwardService.awardManualPoints(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Manual points awarded successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/points/{eventId}")
    public ResponseEntity<ApiResponse<PointEventResponse>> getPointEventById(@PathVariable UUID eventId) {
        PointEventResponse response = pointEventService.getPointEventById(eventId);
        return ResponseEntity.ok(ApiResponse.success(response, "Point event retrieved successfully"));
    }

    @GetMapping("/members/{memberId}/points")
    public ResponseEntity<ApiResponse<MemberPointsSummaryResponse>> getMemberPointsSummary(
            @PathVariable UUID memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        MemberPointsSummaryResponse response = pointEventService.getMemberPointsSummary(memberId, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Member points summary retrieved successfully"));
    }

    @GetMapping("/meetings/{meetingId}/points")
    public ResponseEntity<ApiResponse<MeetingPointsSummaryResponse>> getMeetingPointsSummary(@PathVariable UUID meetingId) {
        MeetingPointsSummaryResponse response = pointEventService.getMeetingPointsSummary(meetingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting points summary retrieved successfully"));
    }

    @GetMapping("/points/leaderboard")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> getLeaderboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntryResponse> response = pointEventService.getLeaderboard(startDate, endDate, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Leaderboard retrieved successfully"));
    }
}
