package com.rathinam.toastmasters.modules.points.dto;

import com.rathinam.toastmasters.modules.points.entity.PointRuleCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public class PointRuleResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Integer points;
    private boolean active;
    private PointRuleCategory category;
    private UUID roleDefinitionId;
    private String roleDefinitionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public PointRuleResponse() {
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
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

    public String getRoleDefinitionName() {
        return roleDefinitionName;
    }

    public void setRoleDefinitionName(String roleDefinitionName) {
        this.roleDefinitionName = roleDefinitionName;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
