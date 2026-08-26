package com.rathinam.toastmasters.modules.ranking.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.ranking.dto.LeaderboardResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MeetingRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MemberRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyPerformanceEntry;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.service.RankingService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<LeaderboardResponse>> getLeaderboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20) Pageable pageable) {
        LeaderboardResponse response = rankingService.getLeaderboard(from, to, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Leaderboard retrieved successfully"));
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<ApiResponse<MonthlyRankingResponse>> getMonthlyRanking(
            @PathVariable int year,
            @PathVariable int month,
            @PageableDefault(size = 20) Pageable pageable) {
        MonthlyRankingResponse response = rankingService.getMonthlyRanking(year, month, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Monthly ranking retrieved successfully"));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<MemberRankingResponse>> getMemberRanking(
            @PathVariable UUID memberId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        MemberRankingResponse response = rankingService.getMemberRanking(memberId, year, month);
        return ResponseEntity.ok(ApiResponse.success(response, "Member ranking retrieved successfully"));
    }

    @GetMapping("/meetings/{meetingId}")
    public ResponseEntity<ApiResponse<MeetingRankingResponse>> getMeetingRankings(
            @PathVariable UUID meetingId) {
        MeetingRankingResponse response = rankingService.getMeetingRankings(meetingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting rankings retrieved successfully"));
    }

    @GetMapping("/member/{memberId}/trends")
    public ResponseEntity<ApiResponse<List<MonthlyPerformanceEntry>>> getMemberMonthlyPerformance(
            @PathVariable UUID memberId) {
        List<MonthlyPerformanceEntry> response = rankingService.getMemberMonthlyPerformance(memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "Member performance trends retrieved successfully"));
    }
}
