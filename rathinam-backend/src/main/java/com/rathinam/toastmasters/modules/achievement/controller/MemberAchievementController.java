package com.rathinam.toastmasters.modules.achievement.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.achievement.dto.BadgeResponse;
import com.rathinam.toastmasters.modules.achievement.dto.MemberAchievementResponse;
import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.achievement.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members/{memberId}")
public class MemberAchievementController {

    private final AchievementService achievementService;
    private final AchievementEvaluationService achievementEvaluationService;

    public MemberAchievementController(AchievementService achievementService,
                                       AchievementEvaluationService achievementEvaluationService) {
        this.achievementService = achievementService;
        this.achievementEvaluationService = achievementEvaluationService;
    }

    @GetMapping("/achievements")
    public ResponseEntity<ApiResponse<List<MemberAchievementResponse>>> getMemberAchievements(
            @PathVariable UUID memberId) {
        List<MemberAchievementResponse> response = achievementService.getMemberAchievements(memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "Member achievements retrieved successfully"));
    }

    @GetMapping("/badges")
    public ResponseEntity<ApiResponse<List<BadgeResponse>>> getMemberBadges(
            @PathVariable UUID memberId) {
        List<BadgeResponse> response = achievementService.getMemberBadges(memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "Member badges retrieved successfully"));
    }

    @PostMapping("/achievements/evaluate")
    public ResponseEntity<ApiResponse<Void>> evaluateMemberAchievements(
            @PathVariable UUID memberId) {
        achievementEvaluationService.evaluateMemberAchievements(memberId);
        return ResponseEntity.ok(ApiResponse.success(null, "Achievement evaluation completed successfully"));
    }
}
