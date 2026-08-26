package com.rathinam.toastmasters.modules.notification.mapper;

import com.rathinam.toastmasters.modules.notification.dto.NotificationResponse;
import com.rathinam.toastmasters.modules.notification.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setMemberId(entity.getMember() != null ? entity.getMember().getId() : null);
        response.setType(entity.getType());
        response.setTitle(entity.getTitle());
        response.setMessage(entity.getMessage());
        response.setMeetingId(entity.getMeeting() != null ? entity.getMeeting().getId() : null);
        response.setSourceType(entity.getSourceType());
        response.setSourceId(entity.getSourceId());
        response.setRead(entity.isRead());
        response.setReadAt(entity.getReadAt());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}
