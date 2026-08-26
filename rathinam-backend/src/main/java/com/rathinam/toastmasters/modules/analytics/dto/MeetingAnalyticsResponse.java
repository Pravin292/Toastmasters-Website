package com.rathinam.toastmasters.modules.analytics.dto;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class MeetingAnalyticsResponse {

    private UUID meetingId;
    private Integer meetingNumber;
    private OffsetDateTime meetingStart;
    private String theme;
    private MeetingType meetingType;
    private MeetingStatus status;
    private long totalAttendanceRecords;
    private long presentCount;
    private long absentCount;
    private long excusedCount;
    private double attendancePercentage;
    private long rolesAssigned;
    private long rolesFilled;
    private long rolesRemaining;
    private int totalPointsAwarded;
    private long participatingMembersCount;

    public MeetingAnalyticsResponse() {
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

    public long getTotalAttendanceRecords() {
        return totalAttendanceRecords;
    }

    public void setTotalAttendanceRecords(long totalAttendanceRecords) {
        this.totalAttendanceRecords = totalAttendanceRecords;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(long presentCount) {
        this.presentCount = presentCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public long getExcusedCount() {
        return excusedCount;
    }

    public void setExcusedCount(long excusedCount) {
        this.excusedCount = excusedCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public long getRolesAssigned() {
        return rolesAssigned;
    }

    public void setRolesAssigned(long rolesAssigned) {
        this.rolesAssigned = rolesAssigned;
    }

    public long getRolesFilled() {
        return rolesFilled;
    }

    public void setRolesFilled(long rolesFilled) {
        this.rolesFilled = rolesFilled;
    }

    public long getRolesRemaining() {
        return rolesRemaining;
    }

    public void setRolesRemaining(long rolesRemaining) {
        this.rolesRemaining = rolesRemaining;
    }

    public int getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(int totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
    }

    public long getParticipatingMembersCount() {
        return participatingMembersCount;
    }

    public void setParticipatingMembersCount(long participatingMembersCount) {
        this.participatingMembersCount = participatingMembersCount;
    }
}
