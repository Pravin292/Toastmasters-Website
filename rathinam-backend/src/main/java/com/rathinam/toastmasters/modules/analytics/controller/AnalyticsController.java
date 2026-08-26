package com.rathinam.toastmasters.modules.analytics.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.analytics.dto.ClubOverviewAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MemberAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MemberMonthlyPerformanceResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MonthlyAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MonthlyReportResponse;
import com.rathinam.toastmasters.modules.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse<MemberAnalyticsResponse>> getMemberAnalytics(@PathVariable UUID memberId) {
        MemberAnalyticsResponse response = analyticsService.getMemberAnalytics(memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "Member analytics retrieved successfully"));
    }

    @GetMapping("/meetings/{meetingId}")
    public ResponseEntity<ApiResponse<MeetingAnalyticsResponse>> getMeetingAnalytics(@PathVariable UUID meetingId) {
        MeetingAnalyticsResponse response = analyticsService.getMeetingAnalytics(meetingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting analytics retrieved successfully"));
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<ClubOverviewAnalyticsResponse>> getClubOverviewAnalytics() {
        ClubOverviewAnalyticsResponse response = analyticsService.getClubOverviewAnalytics();
        return ResponseEntity.ok(ApiResponse.success(response, "Club overview analytics retrieved successfully"));
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<ApiResponse<MonthlyAnalyticsResponse>> getMonthlyAnalytics(
            @PathVariable int year,
            @PathVariable int month) {
        MonthlyAnalyticsResponse response = analyticsService.getMonthlyAnalytics(year, month);
        return ResponseEntity.ok(ApiResponse.success(response, "Monthly analytics retrieved successfully"));
    }

    @GetMapping("/members/{memberId}/performance")
    public ResponseEntity<ApiResponse<List<MemberMonthlyPerformanceResponse>>> getMemberPerformanceTrend(
            @PathVariable UUID memberId,
            @RequestParam(required = false) Integer months) {
        List<MemberMonthlyPerformanceResponse> response = analyticsService.getMemberPerformanceTrend(memberId, months);
        return ResponseEntity.ok(ApiResponse.success(response, "Member performance trend retrieved successfully"));
    }

    @GetMapping("/reports/monthly/{year}/{month}")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {
        MonthlyReportResponse response = analyticsService.generateMonthlyReport(year, month);
        return ResponseEntity.ok(ApiResponse.success(response, "Monthly report generated successfully"));
    }
}
