package com.rathinam.toastmasters.modules.attendance.dto;

import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateAttendanceRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    private OffsetDateTime checkInTime;

    public CreateAttendanceRequest() {
    }

    public CreateAttendanceRequest(UUID memberId, AttendanceStatus status) {
        this.memberId = memberId;
        this.status = status;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(OffsetDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
}
