package com.rathinam.toastmasters.modules.ranking.dto;

import java.util.UUID;

public class RankingEntryResponse {
    private Integer rank;
    private UUID memberId;
    private String displayName;
    private String email;
    private Long points;

    public RankingEntryResponse() {
    }

    public RankingEntryResponse(UUID memberId, String displayName, String email, Long points) {
        this.memberId = memberId;
        this.displayName = displayName;
        this.email = email;
        this.points = points;
    }

    public RankingEntryResponse(Integer rank, UUID memberId, String displayName, String email, Long points) {
        this.rank = rank;
        this.memberId = memberId;
        this.displayName = displayName;
        this.email = email;
        this.points = points;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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
