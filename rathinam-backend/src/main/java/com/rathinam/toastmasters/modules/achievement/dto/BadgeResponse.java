package com.rathinam.toastmasters.modules.achievement.dto;

import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;

import java.time.OffsetDateTime;
import java.util.UUID;

public class BadgeResponse {

    private UUID achievementId;
    private UUID memberId;
    private String badgeName;
    private String description;
    private String icon;
    private AchievementCategory category;
    private OffsetDateTime earnedAt;

    public BadgeResponse() {
    }

    public BadgeResponse(UUID achievementId, UUID memberId, String badgeName, String description, String icon, AchievementCategory category, OffsetDateTime earnedAt) {
        this.achievementId = achievementId;
        this.memberId = memberId;
        this.badgeName = badgeName;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.earnedAt = earnedAt;
    }

    public UUID getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(UUID achievementId) {
        this.achievementId = achievementId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
