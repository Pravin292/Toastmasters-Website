package com.rathinam.toastmasters.modules.points.dto;

import java.util.UUID;

public class LeaderboardEntryResponse {

    private UUID memberId;
    private String memberDisplayName;
    private String memberEmail;
    private Long totalPoints;
    private Integer rank;

    public LeaderboardEntryResponse() {
    }

    public LeaderboardEntryResponse(UUID memberId, String memberDisplayName, String memberEmail, Long totalPoints) {
        this.memberId = memberId;
        this.memberDisplayName = memberDisplayName;
        this.memberEmail = memberEmail;
        this.totalPoints = totalPoints;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getMemberDisplayName() {
        return memberDisplayName;
    }

    public void setMemberDisplayName(String memberDisplayName) {
        this.memberDisplayName = memberDisplayName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
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
}
