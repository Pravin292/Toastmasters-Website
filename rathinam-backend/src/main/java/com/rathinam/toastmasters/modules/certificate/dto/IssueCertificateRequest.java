package com.rathinam.toastmasters.modules.certificate.dto;

import com.rathinam.toastmasters.modules.certificate.entity.CertificateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class IssueCertificateRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Certificate type is required")
    private CertificateType certificateType;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private UUID achievementId;
    private String customCertificateNumber;

    public IssueCertificateRequest() {
    }

    public IssueCertificateRequest(UUID memberId, CertificateType certificateType, String title, String description) {
        this.memberId = memberId;
        this.certificateType = certificateType;
        this.title = title;
        this.description = description;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(UUID achievementId) {
        this.achievementId = achievementId;
    }

    public String getCustomCertificateNumber() {
        return customCertificateNumber;
    }

    public void setCustomCertificateNumber(String customCertificateNumber) {
        this.customCertificateNumber = customCertificateNumber;
    }
}
