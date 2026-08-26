package com.rathinam.toastmasters.modules.points.dto;

import com.rathinam.toastmasters.modules.points.entity.PointRuleCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class UpdatePointRuleRequest {

    @Size(max = 100, message = "Rule name must not exceed 100 characters")
    private String name;

    private String description;

    @Min(value = 0, message = "Automatic rule points must be non-negative")
    private Integer points;

    private Boolean active;

    private PointRuleCategory category;

    private UUID roleDefinitionId;

    public UpdatePointRuleRequest() {
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PointRuleCategory getCategory() {
        return category;
    }

    public void setCategory(PointRuleCategory category) {
        this.category = category;
    }

    public UUID getRoleDefinitionId() {
        return roleDefinitionId;
    }

    public void setRoleDefinitionId(UUID roleDefinitionId) {
        this.roleDefinitionId = roleDefinitionId;
    }
}
