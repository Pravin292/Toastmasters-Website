package com.rathinam.toastmasters.modules.achievement.dto;

import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCriteriaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateAchievementDefinitionRequest {

    @NotBlank(message = "Code is required")
    @Size(min = 2, max = 50, message = "Code must be between 2 and 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Icon is required")
    private String icon;

    @NotNull(message = "Category is required")
    private AchievementCategory category;

    @NotNull(message = "Criteria type is required")
    private AchievementCriteriaType criteriaType;

    private Integer criteriaThreshold;

    private Boolean isRepeatable = false;

    public CreateAchievementDefinitionRequest() {
    }

    public CreateAchievementDefinitionRequest(String code, String name, String description, String icon, AchievementCategory category, AchievementCriteriaType criteriaType, Integer criteriaThreshold) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.criteriaType = criteriaType;
        this.criteriaThreshold = criteriaThreshold;
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
}
