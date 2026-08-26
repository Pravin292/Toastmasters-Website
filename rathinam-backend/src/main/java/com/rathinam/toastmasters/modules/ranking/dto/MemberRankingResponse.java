package com.rathinam.toastmasters.modules.ranking.dto;

import java.util.UUID;

public class MemberRankingResponse {
    private UUID memberId;
    private String displayName;
    private Long totalPoints;
    private Integer rank;
    private Integer year;
    private Integer month;

    public MemberRankingResponse() {
    }

    public MemberRankingResponse(UUID memberId, String displayName, Long totalPoints, Integer rank, Integer year, Integer month) {
        this.memberId = memberId;
        this.displayName = displayName;
        this.totalPoints = totalPoints;
        this.rank = rank;
        this.year = year;
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

    public Long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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
}
