package com.rathinam.toastmasters.modules.member.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.config.security.CustomUserDetails;
import com.rathinam.toastmasters.modules.member.dto.CreateMemberRequest;
import com.rathinam.toastmasters.modules.member.dto.MemberResponse;
import com.rathinam.toastmasters.modules.member.dto.UpdateMemberRequest;
import com.rathinam.toastmasters.modules.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        List<MemberResponse> response = memberService.getAllMembers();
        return ResponseEntity.ok(ApiResponse.success(response, "Member profiles retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Member profile created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable UUID id) {
        MemberResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Member profile retrieved successfully"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberRequest request,
            @AuthenticationPrincipal Object principal) {
        
        MemberResponse existing = memberService.getMemberById(id);
        boolean isOfficerOrAdmin = false;
        String callerEmail = null;

        if (principal instanceof CustomUserDetails customUserDetails) {
            callerEmail = customUserDetails.getUsername();
            isOfficerOrAdmin = customUserDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                                   a.getAuthority().equals("ROLE_PRESIDENT") ||
                                   a.getAuthority().equals("ROLE_OFFICER"));
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            callerEmail = userDetails.getUsername();
            isOfficerOrAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                                   a.getAuthority().equals("ROLE_PRESIDENT") ||
                                   a.getAuthority().equals("ROLE_OFFICER"));
        } else if (principal instanceof String strPrincipal) {
            callerEmail = strPrincipal;
        }

        if (!isOfficerOrAdmin && (callerEmail == null || !callerEmail.equalsIgnoreCase(existing.getEmail()))) {
            throw new AccessDeniedException("You do not have permission to modify this member profile");
        }

        MemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Member profile updated successfully"));
    }
}
