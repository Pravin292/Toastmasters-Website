package com.rathinam.toastmasters.modules.attendance.exception;

import java.util.UUID;

public class DuplicateAttendanceException extends RuntimeException {
    public DuplicateAttendanceException(UUID meetingId, UUID memberId) {
        super("Attendance record already exists for member " + memberId + " at meeting " + meetingId);
    }
}
