package com.rathinam.toastmasters.modules.certificate.dto;

import com.rathinam.toastmasters.modules.certificate.entity.CertificateStatus;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CertificateResponse {

    private UUID id;
    private String certificateNumber;
    private UUID memberId;
    private String memberDisplayName;
    private CertificateType certificateType;
    private String title;
    private String description;
    private OffsetDateTime issuedDate;
    private UUID achievementId;
    private CertificateStatus status;

    public CertificateResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getMemberDisplayName() {
        return memberDisplayName;
    }

    public void setMemberDisplayName(String memberDisplayName) {
        this.memberDisplayName = memberDisplayName;
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

    public OffsetDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(OffsetDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public UUID getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(UUID achievementId) {
        this.achievementId = achievementId;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }
}
