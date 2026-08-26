package com.rathinam.toastmasters.modules.meeting.entity;

import com.rathinam.toastmasters.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "meetings")
public class MeetingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "meeting_number", nullable = false, unique = true)
    private Integer meetingNumber;

    @Column(name = "meeting_start", nullable = false)
    private OffsetDateTime meetingStart;

    @Column(name = "meeting_end")
    private OffsetDateTime meetingEnd;

    @Column(name = "theme", length = 255)
    private String theme;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false, length = 50)
    private MeetingType meetingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private MeetingStatus status = MeetingStatus.SCHEDULED;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public MeetingEntity() {
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
}
