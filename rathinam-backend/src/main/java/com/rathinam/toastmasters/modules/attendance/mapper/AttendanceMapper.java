package com.rathinam.toastmasters.modules.attendance.mapper;

import com.rathinam.toastmasters.modules.attendance.dto.AttendanceResponse;
import com.rathinam.toastmasters.modules.attendance.dto.CreateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.dto.UpdateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceEntity toEntity(CreateAttendanceRequest request, MeetingEntity meeting, MemberEntity member) {
        AttendanceEntity entity = new AttendanceEntity();
        entity.setMeeting(meeting);
        entity.setMember(member);
        entity.setStatus(request.getStatus());
        entity.setCheckInTime(request.getCheckInTime());
        return entity;
    }

    public void updateEntityFromRequest(AttendanceEntity entity, UpdateAttendanceRequest request) {
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getCheckInTime() != null) {
            entity.setCheckInTime(request.getCheckInTime());
        }
    }

    public AttendanceResponse toResponse(AttendanceEntity entity) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(entity.getId());
        if (entity.getMeeting() != null) {
            response.setMeetingId(entity.getMeeting().getId());
            response.setMeetingNumber(entity.getMeeting().getMeetingNumber());
        }
        if (entity.getMember() != null) {
            response.setMemberId(entity.getMember().getId());
            response.setMemberDisplayName(entity.getMember().getDisplayName());
            response.setMemberEmail(entity.getMember().getEmail());
        }
        response.setStatus(entity.getStatus());
        response.setCheckInTime(entity.getCheckInTime());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
