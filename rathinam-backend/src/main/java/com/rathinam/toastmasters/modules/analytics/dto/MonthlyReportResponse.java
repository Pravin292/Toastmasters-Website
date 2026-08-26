package com.rathinam.toastmasters.modules.analytics.dto;

import com.rathinam.toastmasters.modules.ranking.dto.MonthlyChampionResponse;
import com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse;

import java.util.List;

public class MonthlyReportResponse {

    private String reportingPeriod;
    private int year;
    private int month;
    private MeetingReportStats meetingStatistics;
    private AttendanceReportStats attendanceStatistics;
    private PointsReportStats pointsStatistics;
    private List<RankingEntryResponse> topMembers;
    private MonthlyChampionResponse champion;
    private long achievementsEarned;

    public MonthlyReportResponse() {
    }

    public MonthlyReportResponse(String reportingPeriod, int year, int month,
                                 MeetingReportStats meetingStatistics,
                                 AttendanceReportStats attendanceStatistics,
                                 PointsReportStats pointsStatistics,
                                 List<RankingEntryResponse> topMembers,
                                 MonthlyChampionResponse champion,
                                 long achievementsEarned) {
        this.reportingPeriod = reportingPeriod;
        this.year = year;
        this.month = month;
        this.meetingStatistics = meetingStatistics;
        this.attendanceStatistics = attendanceStatistics;
        this.pointsStatistics = pointsStatistics;
        this.topMembers = topMembers;
        this.champion = champion;
        this.achievementsEarned = achievementsEarned;
    }

    public String getReportingPeriod() {
        return reportingPeriod;
    }

    public void setReportingPeriod(String reportingPeriod) {
        this.reportingPeriod = reportingPeriod;
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

    public MeetingReportStats getMeetingStatistics() {
        return meetingStatistics;
    }

    public void setMeetingStatistics(MeetingReportStats meetingStatistics) {
        this.meetingStatistics = meetingStatistics;
    }

    public AttendanceReportStats getAttendanceStatistics() {
        return attendanceStatistics;
    }

    public void setAttendanceStatistics(AttendanceReportStats attendanceStatistics) {
        this.attendanceStatistics = attendanceStatistics;
    }

    public PointsReportStats getPointsStatistics() {
        return pointsStatistics;
    }

    public void setPointsStatistics(PointsReportStats pointsStatistics) {
        this.pointsStatistics = pointsStatistics;
    }

    public List<RankingEntryResponse> getTopMembers() {
        return topMembers;
    }

    public void setTopMembers(List<RankingEntryResponse> topMembers) {
        this.topMembers = topMembers;
    }

    public MonthlyChampionResponse getChampion() {
        return champion;
    }

    public void setChampion(MonthlyChampionResponse champion) {
        this.champion = champion;
    }

    public long getAchievementsEarned() {
        return achievementsEarned;
    }

    public void setAchievementsEarned(long achievementsEarned) {
        this.achievementsEarned = achievementsEarned;
    }

    public static class MeetingReportStats {
        private long totalMeetings;
        private double averageAttendance;

        public MeetingReportStats() {
        }

        public MeetingReportStats(long totalMeetings, double averageAttendance) {
            this.totalMeetings = totalMeetings;
            this.averageAttendance = averageAttendance;
        }

        public long getTotalMeetings() {
            return totalMeetings;
        }

        public void setTotalMeetings(long totalMeetings) {
            this.totalMeetings = totalMeetings;
        }

        public double getAverageAttendance() {
            return averageAttendance;
        }

        public void setAverageAttendance(double averageAttendance) {
            this.averageAttendance = averageAttendance;
        }
    }

    public static class AttendanceReportStats {
        private long totalAttendanceRecords;
        private long presentCount;
        private long absentCount;
        private long excusedCount;

        public AttendanceReportStats() {
        }

        public AttendanceReportStats(long totalAttendanceRecords, long presentCount, long absentCount, long excusedCount) {
            this.totalAttendanceRecords = totalAttendanceRecords;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.excusedCount = excusedCount;
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
    }

    public static class PointsReportStats {
        private int totalPointsAwarded;

        public PointsReportStats() {
        }

        public PointsReportStats(int totalPointsAwarded) {
            this.totalPointsAwarded = totalPointsAwarded;
        }

        public int getTotalPointsAwarded() {
            return totalPointsAwarded;
        }

        public void setTotalPointsAwarded(int totalPointsAwarded) {
            this.totalPointsAwarded = totalPointsAwarded;
        }
    }
}
