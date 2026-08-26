package com.rathinam.toastmasters.modules.meetingrole.exception;

import java.util.UUID;

public class MeetingRoleAssignmentNotFoundException extends RuntimeException {
    public MeetingRoleAssignmentNotFoundException(UUID id) {
        super("Meeting role assignment not found with ID: " + id);
    }
}
