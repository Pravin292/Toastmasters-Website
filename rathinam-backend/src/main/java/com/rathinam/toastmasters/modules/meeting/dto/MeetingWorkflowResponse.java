package com.rathinam.toastmasters.modules.meeting.dto;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class MeetingWorkflowResponse {

    private UUID meetingId;
    private Integer meetingNumber;
    private OffsetDateTime meetingStart;
    private String theme;
    private MeetingType meetingType;
    private MeetingStatus status;

    private boolean canStart;
    private boolean canComplete;

    private AttendanceWorkflowSummary attendanceSummary;
    private RoleWorkflowSummary roleSummary;
    private PointsWorkflowSummary pointsSummary;

    private List<String> workflowWarnings;
    private boolean isAiSummaryAvailable;

    public MeetingWorkflowResponse() {
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

    public OffsetDateTime getMeetingStart() {
        return meetingStart;
    }

    public void setMeetingStart(OffsetDateTime meetingStart) {
        this.meetingStart = meetingStart;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public boolean isCanStart() {
        return canStart;
    }

    public void setCanStart(boolean canStart) {
        this.canStart = canStart;
    }

    public boolean isCanComplete() {
        return canComplete;
    }

    public void setCanComplete(boolean canComplete) {
        this.canComplete = canComplete;
    }

    public AttendanceWorkflowSummary getAttendanceSummary() {
        return attendanceSummary;
    }

    public void setAttendanceSummary(AttendanceWorkflowSummary attendanceSummary) {
        this.attendanceSummary = attendanceSummary;
    }

    public RoleWorkflowSummary getRoleSummary() {
        return roleSummary;
    }

    public void setRoleSummary(RoleWorkflowSummary roleSummary) {
        this.roleSummary = roleSummary;
    }

    public PointsWorkflowSummary getPointsSummary() {
        return pointsSummary;
    }

    public void setPointsSummary(PointsWorkflowSummary pointsSummary) {
        this.pointsSummary = pointsSummary;
    }

    public List<String> getWorkflowWarnings() {
        return workflowWarnings;
    }

    public void setWorkflowWarnings(List<String> workflowWarnings) {
        this.workflowWarnings = workflowWarnings;
    }

    public boolean isAiSummaryAvailable() {
        return isAiSummaryAvailable;
    }

    public void setAiSummaryAvailable(boolean aiSummaryAvailable) {
        isAiSummaryAvailable = aiSummaryAvailable;
    }

    public static class AttendanceWorkflowSummary {
        private long totalRecords;
        private long presentCount;
        private long absentCount;
        private long excusedCount;
        private double attendancePercentage;

        public AttendanceWorkflowSummary() {
        }

        public AttendanceWorkflowSummary(long totalRecords, long presentCount, long absentCount, long excusedCount, double attendancePercentage) {
            this.totalRecords = totalRecords;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.excusedCount = excusedCount;
            this.attendancePercentage = attendancePercentage;
        }

        public long getTotalRecords() { return totalRecords; }
        public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
        public long getPresentCount() { return presentCount; }
        public void setPresentCount(long presentCount) { this.presentCount = presentCount; }
        public long getAbsentCount() { return absentCount; }
        public void setAbsentCount(long absentCount) { this.absentCount = absentCount; }
        public long getExcusedCount() { return excusedCount; }
        public void setExcusedCount(long excusedCount) { this.excusedCount = excusedCount; }
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }

    public static class RoleWorkflowSummary {
        private long rolesAssigned;
        private long rolesFilled;
        private long rolesRemaining;

        public RoleWorkflowSummary() {
        }

        public RoleWorkflowSummary(long rolesAssigned, long rolesFilled, long rolesRemaining) {
            this.rolesAssigned = rolesAssigned;
            this.rolesFilled = rolesFilled;
            this.rolesRemaining = rolesRemaining;
        }

        public long getRolesAssigned() { return rolesAssigned; }
        public void setRolesAssigned(long rolesAssigned) { this.rolesAssigned = rolesAssigned; }
        public long getRolesFilled() { return rolesFilled; }
        public void setRolesFilled(long rolesFilled) { this.rolesFilled = rolesFilled; }
        public long getRolesRemaining() { return rolesRemaining; }
        public void setRolesRemaining(long rolesRemaining) { this.rolesRemaining = rolesRemaining; }
    }

    public static class PointsWorkflowSummary {
        private int totalPointsAwarded;

        public PointsWorkflowSummary() {
        }

        public PointsWorkflowSummary(int totalPointsAwarded) {
            this.totalPointsAwarded = totalPointsAwarded;
        }

        public int getTotalPointsAwarded() { return totalPointsAwarded; }
        public void setTotalPointsAwarded(int totalPointsAwarded) { this.totalPointsAwarded = totalPointsAwarded; }
    }
}
