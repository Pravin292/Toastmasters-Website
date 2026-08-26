package com.rathinam.toastmasters.modules.meeting.exception;

import java.util.UUID;

public class MeetingNotFoundException extends RuntimeException {
    public MeetingNotFoundException(UUID id) {
        super("Meeting not found with ID: " + id);
    }
}
