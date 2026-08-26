package com.rathinam.toastmasters.modules.meetingrole.exception;

import java.util.UUID;

public class InactiveRoleDefinitionException extends RuntimeException {
    public InactiveRoleDefinitionException(UUID roleDefinitionId) {
        super("Role definition with ID '" + roleDefinitionId + "' is inactive and cannot be assigned to new meetings");
    }
}
