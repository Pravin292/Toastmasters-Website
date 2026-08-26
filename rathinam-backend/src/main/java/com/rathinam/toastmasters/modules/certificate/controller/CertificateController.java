package com.rathinam.toastmasters.modules.certificate.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.certificate.dto.CertificateResponse;
import com.rathinam.toastmasters.modules.certificate.dto.IssueCertificateRequest;
import com.rathinam.toastmasters.modules.certificate.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/certificates")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'OFFICER')")
    public ResponseEntity<ApiResponse<CertificateResponse>> issueCertificate(
            @Valid @RequestBody IssueCertificateRequest request) {
        CertificateResponse response = certificateService.issueCertificate(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Certificate issued successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/certificates/{id}")
    public ResponseEntity<ApiResponse<CertificateResponse>> getCertificateById(
            @PathVariable UUID id) {
        CertificateResponse response = certificateService.getCertificateById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Certificate retrieved successfully"));
    }

    @GetMapping("/members/{memberId}/certificates")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getMemberCertificates(
            @PathVariable UUID memberId) {
        List<CertificateResponse> response = certificateService.getMemberCertificates(memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "Member certificates retrieved successfully"));
    }
}
