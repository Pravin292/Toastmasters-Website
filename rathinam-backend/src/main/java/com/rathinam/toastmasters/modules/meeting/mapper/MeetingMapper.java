package com.rathinam.toastmasters.modules.meeting.mapper;

import com.rathinam.toastmasters.modules.meeting.dto.CreateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingResponse;
import com.rathinam.toastmasters.modules.meeting.dto.UpdateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import org.springframework.stereotype.Component;

@Component
public class MeetingMapper {

    public MeetingEntity toEntity(CreateMeetingRequest request) {
        MeetingEntity entity = new MeetingEntity();
        entity.setMeetingNumber(request.getMeetingNumber());
        entity.setMeetingStart(request.getMeetingStart());
        entity.setMeetingEnd(request.getMeetingEnd());
        entity.setTheme(request.getTheme() != null ? request.getTheme().trim() : null);
        entity.setMeetingType(request.getMeetingType());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : MeetingStatus.SCHEDULED);
        entity.setLocation(request.getLocation() != null ? request.getLocation().trim() : null);
        entity.setMeetingUrl(request.getMeetingUrl() != null ? request.getMeetingUrl().trim() : null);
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        return entity;
    }

    public void updateEntityFromRequest(MeetingEntity entity, UpdateMeetingRequest request) {
        if (request.getMeetingNumber() != null) {
            entity.setMeetingNumber(request.getMeetingNumber());
        }
        if (request.getMeetingStart() != null) {
            entity.setMeetingStart(request.getMeetingStart());
        }
        if (request.getMeetingEnd() != null) {
            entity.setMeetingEnd(request.getMeetingEnd());
        }
        if (request.getTheme() != null) {
            entity.setTheme(request.getTheme().trim());
        }
        if (request.getMeetingType() != null) {
            entity.setMeetingType(request.getMeetingType());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getLocation() != null) {
            entity.setLocation(request.getLocation().trim());
        }
        if (request.getMeetingUrl() != null) {
            entity.setMeetingUrl(request.getMeetingUrl().trim());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription().trim());
        }
    }

    public MeetingResponse toResponse(MeetingEntity entity) {
        MeetingResponse response = new MeetingResponse();
        response.setId(entity.getId());
        response.setMeetingNumber(entity.getMeetingNumber());
        response.setMeetingStart(entity.getMeetingStart());
        response.setMeetingEnd(entity.getMeetingEnd());
        response.setTheme(entity.getTheme());
        response.setMeetingType(entity.getMeetingType());
        response.setStatus(entity.getStatus());
        response.setLocation(entity.getLocation());
        response.setMeetingUrl(entity.getMeetingUrl());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
