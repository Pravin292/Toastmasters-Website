package com.rathinam.toastmasters.modules.meetingrole.dto;

import jakarta.validation.constraints.Size;

public class UpdateRoleDefinitionRequest {

    @Size(max = 100, message = "Role name must not exceed 100 characters")
    private String name;

    private String description;

    private Boolean active;

    public UpdateRoleDefinitionRequest() {
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
