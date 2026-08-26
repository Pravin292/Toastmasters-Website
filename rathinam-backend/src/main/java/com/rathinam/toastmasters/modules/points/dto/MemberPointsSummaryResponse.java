package com.rathinam.toastmasters.modules.points.dto;

import org.springframework.data.domain.Page;

import java.util.UUID;

public class MemberPointsSummaryResponse {

    private UUID memberId;
    private String memberDisplayName;
    private String memberEmail;
    private Integer totalPoints;
    private Page<PointEventResponse> events;

    public MemberPointsSummaryResponse() {
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

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Page<PointEventResponse> getEvents() {
        return events;
    }

    public void setEvents(Page<PointEventResponse> events) {
        this.events = events;
    }
}
