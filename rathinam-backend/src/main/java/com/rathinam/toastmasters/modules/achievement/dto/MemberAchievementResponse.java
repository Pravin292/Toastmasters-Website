package com.rathinam.toastmasters.modules.achievement.dto;

import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;

import java.time.OffsetDateTime;
import java.util.UUID;

public class MemberAchievementResponse {

    private UUID id;
    private UUID memberId;
    private String memberDisplayName;
    private UUID achievementDefinitionId;
    private String achievementCode;
    private String achievementName;
    private String icon;
    private AchievementCategory category;
    private OffsetDateTime earnedAt;
    private UUID meetingId;
    private String reason;

    public MemberAchievementResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getMemberDisplayName() {
        return memberDisplayName;
    }

    public void setMemberDisplayName(String memberDisplayName) {
        this.memberDisplayName = memberDisplayName;
    }

    public UUID getAchievementDefinitionId() {
        return achievementDefinitionId;
    }

    public void setAchievementDefinitionId(UUID achievementDefinitionId) {
        this.achievementDefinitionId = achievementDefinitionId;
    }

    public String getAchievementCode() {
        return achievementCode;
    }

    public void setAchievementCode(String achievementCode) {
        this.achievementCode = achievementCode;
    }

    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public AchievementCategory getCategory() {
        return category;
    }

    public void setCategory(AchievementCategory category) {
        this.category = category;
    }

    public OffsetDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(OffsetDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public UUID getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(UUID meetingId) {
        this.meetingId = meetingId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
