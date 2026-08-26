package com.rathinam.toastmasters.modules.notification.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.config.security.CustomUserDetails;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.notification.dto.NotificationResponse;
import com.rathinam.toastmasters.modules.notification.dto.UnreadNotificationCountResponse;
import com.rathinam.toastmasters.modules.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    public NotificationController(NotificationService notificationService,
                                  MemberRepository memberRepository) {
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMemberNotifications(
            @AuthenticationPrincipal Object principal,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID memberId = getCurrentMemberId(principal);
        Page<NotificationResponse> notifications = notificationService.getMemberNotifications(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved successfully"));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal Object principal,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID memberId = getCurrentMemberId(principal);
        Page<NotificationResponse> notifications = notificationService.getUnreadNotifications(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Unread notifications retrieved successfully"));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<UnreadNotificationCountResponse>> countUnreadNotifications(
            @AuthenticationPrincipal Object principal) {
        UUID memberId = getCurrentMemberId(principal);
        UnreadNotificationCountResponse countResponse = notificationService.countUnreadNotifications(memberId);
        return ResponseEntity.ok(ApiResponse.success(countResponse, "Unread notification count retrieved successfully"));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal Object principal) {
        UUID memberId = getCurrentMemberId(principal);
        NotificationResponse response = notificationService.markAsRead(id, memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "Notification marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @AuthenticationPrincipal Object principal) {
        UUID memberId = getCurrentMemberId(principal);
        int updatedCount = notificationService.markAllAsRead(memberId);
        return ResponseEntity.ok(ApiResponse.success(
                String.format("%d notifications marked as read", updatedCount),
                "All notifications marked as read successfully"
        ));
    }

    private UUID getCurrentMemberId(Object principal) {
        if (principal == null) {
            throw new IllegalStateException("Unauthenticated user context");
        }

        String resolvedEmail = null;
        if (principal instanceof CustomUserDetails customUserDetails) {
            resolvedEmail = customUserDetails.getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            resolvedEmail = userDetails.getUsername();
        } else if (principal instanceof String strPrincipal) {
            resolvedEmail = strPrincipal;
        }

        if (resolvedEmail == null || resolvedEmail.isBlank()) {
            throw new IllegalStateException("Unauthenticated user context");
        }

        final String email = resolvedEmail;
        return memberRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new MemberNotFoundException("Member profile not found for account: " + email))
                .getId();
    }
}
