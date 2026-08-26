package com.rathinam.toastmasters.modules.notification.service;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import com.rathinam.toastmasters.modules.notification.entity.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationReminderService {

    private final NotificationService notificationService;
    private final MeetingRepository meetingRepository;
    private final MeetingRoleAssignmentRepository assignmentRepository;

    public NotificationReminderService(NotificationService notificationService,
                                        MeetingRepository meetingRepository,
                                        MeetingRoleAssignmentRepository assignmentRepository) {
        this.notificationService = notificationService;
        this.meetingRepository = meetingRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public void sendMeetingReminder(UUID meetingId) {
        MeetingEntity meeting = meetingRepository.findById(meetingId).orElse(null);
        if (meeting == null) {
            return;
        }

        List<MeetingRoleAssignmentEntity> assignments = assignmentRepository.findByMeetingId(meetingId);
        for (MeetingRoleAssignmentEntity assignment : assignments) {
            notificationService.notifyMember(
                    assignment.getMember().getId(),
                    NotificationType.MEETING_REMINDER,
                    "Reminder: Upcoming Meeting #" + meeting.getMeetingNumber(),
                    String.format("Reminder: Meeting #%d (%s) is scheduled for %s. Your assigned role is %s.",
                            meeting.getMeetingNumber(),
                            meeting.getTheme() != null ? meeting.getTheme() : "Regular Meeting",
                            meeting.getMeetingStart(),
                            assignment.getRoleDefinition().getName()),
                    meeting.getId(),
                    "MEETING_REMINDER",
                    meeting.getId()
            );
        }
    }
}
