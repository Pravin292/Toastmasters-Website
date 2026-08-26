package com.rathinam.toastmasters.modules.points.dto;

import com.rathinam.toastmasters.modules.points.entity.PointRuleCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreatePointRuleRequest {

    @NotBlank(message = "Rule code is required")
    @Size(max = 50, message = "Rule code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Rule name is required")
    @Size(max = 100, message = "Rule name must not exceed 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Points value is required")
    @Min(value = 0, message = "Automatic rule points must be non-negative")
    private Integer points;

    private Boolean active = true;

    @NotNull(message = "Category is required")
    private PointRuleCategory category;

    private UUID roleDefinitionId;

    public CreatePointRuleRequest() {
    }

    public CreatePointRuleRequest(String code, String name, Integer points, PointRuleCategory category) {
        this.code = code;
        this.name = name;
        this.points = points;
        this.category = category;
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
