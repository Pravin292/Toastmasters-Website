package com.rathinam.toastmasters.modules.meeting.dto;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public class UpdateMeetingRequest {

    @Min(value = 1, message = "Meeting number must be positive")
    private Integer meetingNumber;

    private OffsetDateTime meetingStart;

    private OffsetDateTime meetingEnd;

    @Size(max = 255, message = "Theme must not exceed 255 characters")
    private String theme;

    private MeetingType meetingType;

    private MeetingStatus status;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Size(max = 500, message = "Meeting URL must not exceed 500 characters")
    private String meetingUrl;

    private String description;

    public UpdateMeetingRequest() {
    }

    public Integer getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(Integer meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public OffsetDateTime getMeetingStart() {
        return meetingStart;
    }

    public void setMeetingStart(OffsetDateTime meetingStart) {
        this.meetingStart = meetingStart;
    }

    public OffsetDateTime getMeetingEnd() {
        return meetingEnd;
    }

    public void setMeetingEnd(OffsetDateTime meetingEnd) {
        this.meetingEnd = meetingEnd;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
