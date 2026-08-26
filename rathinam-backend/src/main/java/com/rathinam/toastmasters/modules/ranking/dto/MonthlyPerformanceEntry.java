package com.rathinam.toastmasters.modules.ranking.dto;

public class MonthlyPerformanceEntry {
    private Integer year;
    private Integer month;
    private Long points;

    public MonthlyPerformanceEntry() {
    }

    public MonthlyPerformanceEntry(Integer year, Integer month, Long points) {
        this.year = year;
        this.month = month;
        this.points = points;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}
