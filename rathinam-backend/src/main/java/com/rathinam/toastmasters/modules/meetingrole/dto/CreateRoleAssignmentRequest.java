package com.rathinam.toastmasters.modules.meetingrole.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateRoleAssignmentRequest {

    @NotNull(message = "Role definition ID is required")
    private UUID roleDefinitionId;

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    public CreateRoleAssignmentRequest() {
    }

    public CreateRoleAssignmentRequest(UUID roleDefinitionId, UUID memberId) {
        this.roleDefinitionId = roleDefinitionId;
        this.memberId = memberId;
    }

    public UUID getRoleDefinitionId() {
        return roleDefinitionId;
    }

    public void setRoleDefinitionId(UUID roleDefinitionId) {
        this.roleDefinitionId = roleDefinitionId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }
}
