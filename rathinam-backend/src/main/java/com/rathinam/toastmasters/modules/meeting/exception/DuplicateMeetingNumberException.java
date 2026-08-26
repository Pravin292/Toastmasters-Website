package com.rathinam.toastmasters.modules.meeting.exception;

public class DuplicateMeetingNumberException extends RuntimeException {
    public DuplicateMeetingNumberException(Integer meetingNumber) {
        super("A meeting with number #" + meetingNumber + " already exists");
    }
}
