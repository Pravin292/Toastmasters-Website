package com.rathinam.toastmasters.modules.meeting.dto;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MeetingResponse {

    private UUID id;
    private Integer meetingNumber;
    private OffsetDateTime meetingStart;
    private OffsetDateTime meetingEnd;
    private String theme;
    private MeetingType meetingType;
    private MeetingStatus status;
    private String location;
    private String meetingUrl;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public MeetingResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
