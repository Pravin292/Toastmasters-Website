package com.rathinam.toastmasters.modules.analytics.dto;

import java.util.UUID;

public class MemberAnalyticsResponse {

    private UUID memberId;
    private String displayName;
    private String email;
    private long totalMeetingsAttended;
    private double attendancePercentage;
    private long totalRolesPerformed;
    private int totalPoints;
    private Integer currentRank;
    private long achievementsEarned;

    public MemberAnalyticsResponse() {
    }

    public MemberAnalyticsResponse(UUID memberId, String displayName, String email,
                                   long totalMeetingsAttended, double attendancePercentage,
                                   long totalRolesPerformed, int totalPoints,
                                   Integer currentRank, long achievementsEarned) {
        this.memberId = memberId;
        this.displayName = displayName;
        this.email = email;
        this.totalMeetingsAttended = totalMeetingsAttended;
        this.attendancePercentage = attendancePercentage;
        this.totalRolesPerformed = totalRolesPerformed;
        this.totalPoints = totalPoints;
        this.currentRank = currentRank;
        this.achievementsEarned = achievementsEarned;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTotalMeetingsAttended() {
        return totalMeetingsAttended;
    }

    public void setTotalMeetingsAttended(long totalMeetingsAttended) {
        this.totalMeetingsAttended = totalMeetingsAttended;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public long getTotalRolesPerformed() {
        return totalRolesPerformed;
    }

    public void setTotalRolesPerformed(long totalRolesPerformed) {
        this.totalRolesPerformed = totalRolesPerformed;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(Integer currentRank) {
        this.currentRank = currentRank;
    }

    public long getAchievementsEarned() {
        return achievementsEarned;
    }

    public void setAchievementsEarned(long achievementsEarned) {
        this.achievementsEarned = achievementsEarned;
    }
}
