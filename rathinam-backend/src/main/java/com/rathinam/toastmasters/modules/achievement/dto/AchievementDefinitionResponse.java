package com.rathinam.toastmasters.modules.achievement.dto;

import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCriteriaType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AchievementDefinitionResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private AchievementCategory category;
    private AchievementCriteriaType criteriaType;
    private Integer criteriaThreshold;
    private Boolean isRepeatable;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AchievementDefinitionResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public AchievementCriteriaType getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(AchievementCriteriaType criteriaType) {
        this.criteriaType = criteriaType;
    }

    public Integer getCriteriaThreshold() {
        return criteriaThreshold;
    }

    public void setCriteriaThreshold(Integer criteriaThreshold) {
        this.criteriaThreshold = criteriaThreshold;
    }

    public Boolean getRepeatable() {
        return isRepeatable;
    }

    public void setRepeatable(Boolean repeatable) {
        isRepeatable = repeatable;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
