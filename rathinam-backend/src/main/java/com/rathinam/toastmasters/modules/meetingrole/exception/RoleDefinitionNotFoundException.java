package com.rathinam.toastmasters.modules.meetingrole.exception;

import java.util.UUID;

public class RoleDefinitionNotFoundException extends RuntimeException {
    public RoleDefinitionNotFoundException(UUID id) {
        super("Role definition not found with ID: " + id);
    }
}
