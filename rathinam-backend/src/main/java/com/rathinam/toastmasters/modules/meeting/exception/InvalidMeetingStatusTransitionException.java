package com.rathinam.toastmasters.modules.meeting.exception;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;

public class InvalidMeetingStatusTransitionException extends RuntimeException {

    public InvalidMeetingStatusTransitionException(String message) {
        super(message);
    }

    public InvalidMeetingStatusTransitionException(MeetingStatus currentStatus, MeetingStatus targetStatus) {
        super(String.format("Invalid meeting status transition from %s to %s", currentStatus, targetStatus));
    }
}
