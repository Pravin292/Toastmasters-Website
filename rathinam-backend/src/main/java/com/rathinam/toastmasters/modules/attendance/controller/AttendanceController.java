package com.rathinam.toastmasters.modules.attendance.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.attendance.dto.AttendanceResponse;
import com.rathinam.toastmasters.modules.attendance.dto.CreateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.dto.UpdateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/meetings/{meetingId}/attendance")
    public ResponseEntity<ApiResponse<AttendanceResponse>> recordAttendance(
            @PathVariable UUID meetingId,
            @Valid @RequestBody CreateAttendanceRequest request) {
        AttendanceResponse response = attendanceService.recordAttendance(meetingId, request);
        return new ResponseEntity<>(ApiResponse.success(response, "Attendance recorded successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/meetings/{meetingId}/attendance")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getMeetingAttendance(@PathVariable UUID meetingId) {
        List<AttendanceResponse> response = attendanceService.getMeetingAttendance(meetingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Meeting attendance retrieved successfully"));
    }

    @GetMapping("/attendance/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(@PathVariable UUID attendanceId) {
        AttendanceResponse response = attendanceService.getAttendanceById(attendanceId);
        return ResponseEntity.ok(ApiResponse.success(response, "Attendance record retrieved successfully"));
    }

    @PatchMapping("/attendance/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable UUID attendanceId,
            @Valid @RequestBody UpdateAttendanceRequest request) {
        AttendanceResponse response = attendanceService.updateAttendance(attendanceId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Attendance record updated successfully"));
    }
}
