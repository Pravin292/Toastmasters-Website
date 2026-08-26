package com.rathinam.toastmasters.modules.points.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PointEventResponse {

    private UUID id;
    private UUID memberId;
    private String memberDisplayName;
    private String memberEmail;
    private UUID meetingId;
    private Integer meetingNumber;
    private UUID pointRuleId;
    private String pointRuleCode;
    private String pointRuleName;
    private Integer points;
    private String reason;
    private String sourceType;
    private UUID sourceId;
    private LocalDateTime createdAt;
    private String createdBy;

    public PointEventResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public UUID getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(UUID meetingId) {
        this.meetingId = meetingId;
    }

    public Integer getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(Integer meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public UUID getPointRuleId() {
        return pointRuleId;
    }

    public void setPointRuleId(UUID pointRuleId) {
        this.pointRuleId = pointRuleId;
    }

    public String getPointRuleCode() {
        return pointRuleCode;
    }

    public void setPointRuleCode(String pointRuleCode) {
        this.pointRuleCode = pointRuleCode;
    }

    public String getPointRuleName() {
        return pointRuleName;
    }

    public void setPointRuleName(String pointRuleName) {
        this.pointRuleName = pointRuleName;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
