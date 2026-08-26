package com.rathinam.toastmasters.modules.points.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class ManualPointAdjustmentRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    private UUID meetingId;

    @NotNull(message = "Points value is required")
    private Integer points;

    @NotBlank(message = "Reason is required for manual point adjustments")
    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;

    public ManualPointAdjustmentRequest() {
    }

    public ManualPointAdjustmentRequest(UUID memberId, Integer points, String reason) {
        this.memberId = memberId;
        this.points = points;
        this.reason = reason;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public UUID getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(UUID meetingId) {
        this.meetingId = meetingId;
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
}
