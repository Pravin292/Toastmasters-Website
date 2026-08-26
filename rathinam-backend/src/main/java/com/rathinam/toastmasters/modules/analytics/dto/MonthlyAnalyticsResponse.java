package com.rathinam.toastmasters.modules.analytics.dto;

import com.rathinam.toastmasters.modules.ranking.dto.MonthlyChampionResponse;
import com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse;

import java.util.List;

public class MonthlyAnalyticsResponse {

    private int year;
    private int month;
    private long totalMeetings;
    private long totalAttendance;
    private double averageAttendance;
    private int totalPointsAwarded;
    private long activeMembersCount;
    private List<RankingEntryResponse> topPerformers;
    private MonthlyChampionResponse monthlyChampion;
    private long achievementsEarned;

    public MonthlyAnalyticsResponse() {
    }

    public MonthlyAnalyticsResponse(int year, int month, long totalMeetings, long totalAttendance,
                                    double averageAttendance, int totalPointsAwarded,
                                    long activeMembersCount, List<RankingEntryResponse> topPerformers,
                                    MonthlyChampionResponse monthlyChampion, long achievementsEarned) {
        this.year = year;
        this.month = month;
        this.totalMeetings = totalMeetings;
        this.totalAttendance = totalAttendance;
        this.averageAttendance = averageAttendance;
        this.totalPointsAwarded = totalPointsAwarded;
        this.activeMembersCount = activeMembersCount;
        this.topPerformers = topPerformers;
        this.monthlyChampion = monthlyChampion;
        this.achievementsEarned = achievementsEarned;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getTotalMeetings() {
        return totalMeetings;
    }

    public void setTotalMeetings(long totalMeetings) {
        this.totalMeetings = totalMeetings;
    }

    public long getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(long totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public double getAverageAttendance() {
        return averageAttendance;
    }

    public void setAverageAttendance(double averageAttendance) {
        this.averageAttendance = averageAttendance;
    }

    public int getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(int totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
    }

    public long getActiveMembersCount() {
        return activeMembersCount;
    }

    public void setActiveMembersCount(long activeMembersCount) {
        this.activeMembersCount = activeMembersCount;
    }

    public List<RankingEntryResponse> getTopPerformers() {
        return topPerformers;
    }

    public void setTopPerformers(List<RankingEntryResponse> topPerformers) {
        this.topPerformers = topPerformers;
    }

    public MonthlyChampionResponse getMonthlyChampion() {
        return monthlyChampion;
    }

    public void setMonthlyChampion(MonthlyChampionResponse monthlyChampion) {
        this.monthlyChampion = monthlyChampion;
    }

    public long getAchievementsEarned() {
        return achievementsEarned;
    }

    public void setAchievementsEarned(long achievementsEarned) {
        this.achievementsEarned = achievementsEarned;
    }
}
