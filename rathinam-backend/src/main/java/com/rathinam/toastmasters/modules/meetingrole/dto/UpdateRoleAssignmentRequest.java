package com.rathinam.toastmasters.modules.meetingrole.dto;

import java.util.UUID;

public class UpdateRoleAssignmentRequest {

    private UUID roleDefinitionId;
    private UUID memberId;

    public UpdateRoleAssignmentRequest() {
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
