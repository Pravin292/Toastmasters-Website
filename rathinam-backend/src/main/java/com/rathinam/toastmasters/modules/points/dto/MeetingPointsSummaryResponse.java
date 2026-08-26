package com.rathinam.toastmasters.modules.points.dto;

import java.util.List;
import java.util.UUID;

public class MeetingPointsSummaryResponse {

    private UUID meetingId;
    private Integer meetingNumber;
    private Integer totalPointsAwarded;
    private List<PointEventResponse> events;

    public MeetingPointsSummaryResponse() {
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

    public Integer getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(Integer totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
    }

    public List<PointEventResponse> getEvents() {
        return events;
    }

    public void setEvents(List<PointEventResponse> events) {
        this.events = events;
    }
}
