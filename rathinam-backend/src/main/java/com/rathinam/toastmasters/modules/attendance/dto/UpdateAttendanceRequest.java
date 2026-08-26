package com.rathinam.toastmasters.modules.attendance.dto;

import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;

import java.time.OffsetDateTime;

public class UpdateAttendanceRequest {

    private AttendanceStatus status;
    private OffsetDateTime checkInTime;

    public UpdateAttendanceRequest() {
    }

    public UpdateAttendanceRequest(AttendanceStatus status) {
        this.status = status;
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
