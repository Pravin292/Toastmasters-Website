package com.rathinam.toastmasters.modules.ai.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.ai.dto.GenerateMeetingSummaryRequest;
import com.rathinam.toastmasters.modules.ai.dto.MeetingSummaryResponse;
import com.rathinam.toastmasters.modules.ai.service.AiMeetingSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiMeetingSummaryService summaryService;

    public AiController(AiMeetingSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping("/meetings/{meetingId}/summary")
    public ResponseEntity<ApiResponse<MeetingSummaryResponse>> generateMeetingSummary(
            @PathVariable UUID meetingId,
            @RequestBody(required = false) GenerateMeetingSummaryRequest request) {
        MeetingSummaryResponse response = summaryService.generateMeetingSummary(meetingId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "AI meeting summary generated successfully"));
    }
}
