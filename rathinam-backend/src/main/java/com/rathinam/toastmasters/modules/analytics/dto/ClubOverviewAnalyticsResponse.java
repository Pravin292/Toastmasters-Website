package com.rathinam.toastmasters.modules.analytics.dto;

import com.rathinam.toastmasters.modules.ranking.dto.MonthlyChampionResponse;

public class ClubOverviewAnalyticsResponse {

    private long totalMembers;
    private long activeMembers;
    private long totalMeetings;
    private long totalAttendanceRecords;
    private double averageAttendancePerMeeting;
    private int totalPointsAwarded;
    private long totalAchievementsEarned;
    private MonthlyChampionResponse currentMonthlyChampion;

    public ClubOverviewAnalyticsResponse() {
    }

    public ClubOverviewAnalyticsResponse(long totalMembers, long activeMembers, long totalMeetings,
                                        long totalAttendanceRecords, double averageAttendancePerMeeting,
                                        int totalPointsAwarded, long totalAchievementsEarned,
                                        MonthlyChampionResponse currentMonthlyChampion) {
        this.totalMembers = totalMembers;
        this.activeMembers = activeMembers;
        this.totalMeetings = totalMeetings;
        this.totalAttendanceRecords = totalAttendanceRecords;
        this.averageAttendancePerMeeting = averageAttendancePerMeeting;
        this.totalPointsAwarded = totalPointsAwarded;
        this.totalAchievementsEarned = totalAchievementsEarned;
        this.currentMonthlyChampion = currentMonthlyChampion;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(long activeMembers) {
        this.activeMembers = activeMembers;
    }

    public long getTotalMeetings() {
        return totalMeetings;
    }

    public void setTotalMeetings(long totalMeetings) {
        this.totalMeetings = totalMeetings;
    }

    public long getTotalAttendanceRecords() {
        return totalAttendanceRecords;
    }

    public void setTotalAttendanceRecords(long totalAttendanceRecords) {
        this.totalAttendanceRecords = totalAttendanceRecords;
    }

    public double getAverageAttendancePerMeeting() {
        return averageAttendancePerMeeting;
    }

    public void setAverageAttendancePerMeeting(double averageAttendancePerMeeting) {
        this.averageAttendancePerMeeting = averageAttendancePerMeeting;
    }

    public int getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(int totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
    }

    public long getTotalAchievementsEarned() {
        return totalAchievementsEarned;
    }

    public void setTotalAchievementsEarned(long totalAchievementsEarned) {
        this.totalAchievementsEarned = totalAchievementsEarned;
    }

    public MonthlyChampionResponse getCurrentMonthlyChampion() {
        return currentMonthlyChampion;
    }

    public void setCurrentMonthlyChampion(MonthlyChampionResponse currentMonthlyChampion) {
        this.currentMonthlyChampion = currentMonthlyChampion;
    }
}
