package com.rathinam.toastmasters.modules.analytics.dto;

public class MemberMonthlyPerformanceResponse {

    private int year;
    private int month;
    private int points;
    private long attendanceCount;
    private long rolesCount;

    public MemberMonthlyPerformanceResponse() {
    }

    public MemberMonthlyPerformanceResponse(int year, int month, int points, long attendanceCount, long rolesCount) {
        this.year = year;
        this.month = month;
        this.points = points;
        this.attendanceCount = attendanceCount;
        this.rolesCount = rolesCount;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(long attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public long getRolesCount() {
        return rolesCount;
    }

    public void setRolesCount(long rolesCount) {
        this.rolesCount = rolesCount;
    }
}
