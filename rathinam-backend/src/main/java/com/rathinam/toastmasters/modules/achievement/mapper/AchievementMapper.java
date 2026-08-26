package com.rathinam.toastmasters.modules.achievement.mapper;

import com.rathinam.toastmasters.modules.achievement.dto.AchievementDefinitionResponse;
import com.rathinam.toastmasters.modules.achievement.dto.BadgeResponse;
import com.rathinam.toastmasters.modules.achievement.dto.MemberAchievementResponse;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import com.rathinam.toastmasters.modules.achievement.entity.MemberAchievementEntity;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {

    public AchievementDefinitionResponse toDefinitionResponse(AchievementDefinitionEntity entity) {
        if (entity == null) return null;
        AchievementDefinitionResponse response = new AchievementDefinitionResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setIcon(entity.getIcon());
        response.setCategory(entity.getCategory());
        response.setCriteriaType(entity.getCriteriaType());
        response.setCriteriaThreshold(entity.getCriteriaThreshold());
        response.setRepeatable(entity.getRepeatable());
        response.setActive(entity.getActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public MemberAchievementResponse toMemberAchievementResponse(MemberAchievementEntity entity) {
        if (entity == null) return null;
        MemberAchievementResponse response = new MemberAchievementResponse();
        response.setId(entity.getId());
        if (entity.getMember() != null) {
            response.setMemberId(entity.getMember().getId());
            response.setMemberDisplayName(entity.getMember().getDisplayName());
        }
        if (entity.getAchievementDefinition() != null) {
            response.setAchievementDefinitionId(entity.getAchievementDefinition().getId());
            response.setAchievementCode(entity.getAchievementDefinition().getCode());
            response.setAchievementName(entity.getAchievementDefinition().getName());
            response.setIcon(entity.getAchievementDefinition().getIcon());
            response.setCategory(entity.getAchievementDefinition().getCategory());
        }
        response.setEarnedAt(entity.getEarnedAt());
        if (entity.getMeeting() != null) {
            response.setMeetingId(entity.getMeeting().getId());
        }
        response.setReason(entity.getReason());
        return response;
    }

    public BadgeResponse toBadgeResponse(MemberAchievementEntity entity) {
        if (entity == null) return null;
        return new BadgeResponse(
                entity.getId(),
                entity.getMember() != null ? entity.getMember().getId() : null,
                entity.getAchievementDefinition() != null ? entity.getAchievementDefinition().getName() : null,
                entity.getAchievementDefinition() != null ? entity.getAchievementDefinition().getDescription() : null,
                entity.getAchievementDefinition() != null ? entity.getAchievementDefinition().getIcon() : null,
                entity.getAchievementDefinition() != null ? entity.getAchievementDefinition().getCategory() : null,
                entity.getEarnedAt()
        );
    }
}
