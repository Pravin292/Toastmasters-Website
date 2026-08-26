package com.rathinam.toastmasters.modules.achievement.entity;

import com.rathinam.toastmasters.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "achievement_definitions")
public class AchievementDefinitionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AchievementCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_type", nullable = false, length = 50)
    private AchievementCriteriaType criteriaType;

    @Column(name = "criteria_threshold")
    private Integer criteriaThreshold;

    @Column(name = "is_repeatable", nullable = false)
    private Boolean isRepeatable = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public AchievementDefinitionEntity() {
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
}
