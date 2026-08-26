package com.rathinam.toastmasters.modules.meetingrole.mapper;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleAssignmentRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleAssignmentResponse;
import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import org.springframework.stereotype.Component;

@Component
public class MeetingRoleAssignmentMapper {

    public MeetingRoleAssignmentEntity toEntity(CreateRoleAssignmentRequest request, MeetingEntity meeting, RoleDefinitionEntity roleDefinition, MemberEntity member) {
        MeetingRoleAssignmentEntity entity = new MeetingRoleAssignmentEntity();
        entity.setMeeting(meeting);
        entity.setRoleDefinition(roleDefinition);
        entity.setMember(member);
        return entity;
    }

    public RoleAssignmentResponse toResponse(MeetingRoleAssignmentEntity entity) {
        RoleAssignmentResponse response = new RoleAssignmentResponse();
        response.setId(entity.getId());
        if (entity.getMeeting() != null) {
            response.setMeetingId(entity.getMeeting().getId());
            response.setMeetingNumber(entity.getMeeting().getMeetingNumber());
        }
        if (entity.getRoleDefinition() != null) {
            response.setRoleDefinitionId(entity.getRoleDefinition().getId());
            response.setRoleName(entity.getRoleDefinition().getName());
        }
        if (entity.getMember() != null) {
            response.setMemberId(entity.getMember().getId());
            response.setMemberDisplayName(entity.getMember().getDisplayName());
            response.setMemberEmail(entity.getMember().getEmail());
        }
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
