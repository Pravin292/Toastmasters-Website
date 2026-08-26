package com.rathinam.toastmasters.modules.ranking.dto;

import java.util.List;
import java.util.UUID;

public class MeetingRankingResponse {
    private UUID meetingId;
    private Integer meetingNumber;
    private List<RankingEntryResponse> rankings;

    public MeetingRankingResponse() {
    }

    public MeetingRankingResponse(UUID meetingId, Integer meetingNumber, List<RankingEntryResponse> rankings) {
        this.meetingId = meetingId;
        this.meetingNumber = meetingNumber;
        this.rankings = rankings;
    }

    public UUID getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(UUID meetingId) {
        this.meetingId = meetingId;
    }

    public Integer getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(Integer meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public List<RankingEntryResponse> getRankings() {
        return rankings;
    }

    public void setRankings(List<RankingEntryResponse> rankings) {
        this.rankings = rankings;
    }
}
