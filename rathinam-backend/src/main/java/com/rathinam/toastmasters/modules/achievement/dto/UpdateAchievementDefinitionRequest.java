package com.rathinam.toastmasters.modules.achievement.dto;

public class UpdateAchievementDefinitionRequest {

    private String name;
    private String description;
    private String icon;
    private Boolean isActive;
    private Boolean isRepeatable;

    public UpdateAchievementDefinitionRequest() {
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getRepeatable() {
        return isRepeatable;
    }

    public void setRepeatable(Boolean repeatable) {
        isRepeatable = repeatable;
    }
}
