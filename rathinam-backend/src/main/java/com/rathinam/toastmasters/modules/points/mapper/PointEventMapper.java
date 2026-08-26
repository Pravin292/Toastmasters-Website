package com.rathinam.toastmasters.modules.points.mapper;

import com.rathinam.toastmasters.modules.points.dto.PointEventResponse;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import org.springframework.stereotype.Component;

@Component
public class PointEventMapper {

    public PointEventResponse toResponse(PointEventEntity entity) {
        PointEventResponse response = new PointEventResponse();
        response.setId(entity.getId());
        if (entity.getMember() != null) {
            response.setMemberId(entity.getMember().getId());
            response.setMemberDisplayName(entity.getMember().getDisplayName());
            response.setMemberEmail(entity.getMember().getEmail());
        }
        if (entity.getMeeting() != null) {
            response.setMeetingId(entity.getMeeting().getId());
            response.setMeetingNumber(entity.getMeeting().getMeetingNumber());
        }
        if (entity.getPointRule() != null) {
            response.setPointRuleId(entity.getPointRule().getId());
            response.setPointRuleCode(entity.getPointRule().getCode());
            response.setPointRuleName(entity.getPointRule().getName());
        }
        response.setPoints(entity.getPoints());
        response.setReason(entity.getReason());
        response.setSourceType(entity.getSourceType());
        response.setSourceId(entity.getSourceId());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        return response;
    }
}
