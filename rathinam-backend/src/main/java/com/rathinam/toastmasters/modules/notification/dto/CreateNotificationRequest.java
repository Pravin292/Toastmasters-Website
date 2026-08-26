package com.rathinam.toastmasters.modules.notification.dto;

import com.rathinam.toastmasters.modules.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateNotificationRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private UUID meetingId;
    private String sourceType;
    private UUID sourceId;

    public CreateNotificationRequest() {
    }

    public CreateNotificationRequest(UUID memberId, NotificationType type, String title, String message, UUID meetingId, String sourceType, UUID sourceId) {
        this.memberId = memberId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.meetingId = meetingId;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

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
}
