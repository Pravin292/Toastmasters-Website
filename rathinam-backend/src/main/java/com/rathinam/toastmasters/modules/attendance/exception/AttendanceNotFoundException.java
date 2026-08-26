package com.rathinam.toastmasters.modules.attendance.exception;

import java.util.UUID;

public class AttendanceNotFoundException extends RuntimeException {
    public AttendanceNotFoundException(UUID id) {
        super("Attendance record not found with ID: " + id);
    }
}
