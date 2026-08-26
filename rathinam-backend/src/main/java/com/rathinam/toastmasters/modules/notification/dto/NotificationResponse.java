package com.rathinam.toastmasters.modules.notification.dto;

import com.rathinam.toastmasters.modules.notification.entity.NotificationType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class NotificationResponse {

    private UUID id;
    private UUID memberId;
    private NotificationType type;
    private String title;
    private String message;
    private UUID meetingId;
    private String sourceType;
    private UUID sourceId;
    private boolean read;
    private OffsetDateTime readAt;
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMemberId() { return memberId; }
    public void setMemberId(UUID memberId) { this.memberId = memberId; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public UUID getMeetingId() { return meetingId; }
    public void setMeetingId(UUID meetingId) { this.meetingId = meetingId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public UUID getSourceId() { return sourceId; }
    public void setSourceId(UUID sourceId) { this.sourceId = sourceId; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public OffsetDateTime getReadAt() { return readAt; }
    public void setReadAt(OffsetDateTime readAt) { this.readAt = readAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
