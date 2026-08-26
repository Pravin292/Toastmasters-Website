package com.rathinam.toastmasters.modules.ranking.dto;

import java.util.UUID;

public class MonthlyChampionResponse {
    private Integer year;
    private Integer month;
    private UUID memberId;
    private String displayName;
    private String email;
    private Long points;

    public MonthlyChampionResponse() {
    }

    public MonthlyChampionResponse(Integer year, Integer month, UUID memberId, String displayName, String email, Long points) {
        this.year = year;
        this.month = month;
        this.memberId = memberId;
        this.displayName = displayName;
        this.email = email;
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

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}
