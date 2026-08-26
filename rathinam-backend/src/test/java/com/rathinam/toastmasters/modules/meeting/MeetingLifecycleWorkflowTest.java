package com.rathinam.toastmasters.modules.meeting;

import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.service.AnalyticsService;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingResponse;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingWorkflowResponse;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;
import com.rathinam.toastmasters.modules.meeting.exception.InvalidMeetingStatusTransitionException;
import com.rathinam.toastmasters.modules.meeting.mapper.MeetingMapper;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meeting.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingLifecycleWorkflowTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Spy
    private MeetingMapper meetingMapper;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private MeetingService meetingService;

    private UUID meetingId;
    private MeetingEntity meetingEntity;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(101);
        meetingEntity.setMeetingStart(OffsetDateTime.now());
        meetingEntity.setMeetingType(MeetingType.REGULAR);
        meetingEntity.setStatus(MeetingStatus.SCHEDULED);
    }

    @Test
    void startMeeting_ScheduledToInProgress_Success() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(meetingRepository.save(any(MeetingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MeetingResponse response = meetingService.startMeeting(meetingId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(MeetingStatus.IN_PROGRESS);
    }

    @Test
    void completeMeeting_InProgressToCompleted_Success() {
        meetingEntity.setStatus(MeetingStatus.IN_PROGRESS);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(meetingRepository.save(any(MeetingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MeetingResponse response = meetingService.completeMeeting(meetingId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(MeetingStatus.COMPLETED);
    }

    @Test
    void startMeeting_AlreadyCompleted_ThrowsInvalidTransition() {
        meetingEntity.setStatus(MeetingStatus.COMPLETED);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));

        assertThatThrownBy(() -> meetingService.startMeeting(meetingId))
                .isInstanceOf(InvalidMeetingStatusTransitionException.class);
    }

    @Test
    void completeMeeting_DirectlyFromScheduled_ThrowsInvalidTransition() {
        meetingEntity.setStatus(MeetingStatus.SCHEDULED);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));

        assertThatThrownBy(() -> meetingService.completeMeeting(meetingId))
                .isInstanceOf(InvalidMeetingStatusTransitionException.class);
    }

    @Test
    void getMeetingWorkflow_Success() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));

        MeetingAnalyticsResponse analytics = new MeetingAnalyticsResponse();
        analytics.setTotalAttendanceRecords(0);
        analytics.setRolesAssigned(0);
        analytics.setRolesRemaining(5);
        when(analyticsService.getMeetingAnalytics(meetingId)).thenReturn(analytics);

        MeetingWorkflowResponse workflow = meetingService.getMeetingWorkflow(meetingId);

        assertThat(workflow).isNotNull();
        assertThat(workflow.isCanStart()).isTrue();
        assertThat(workflow.isCanComplete()).isFalse();
        assertThat(workflow.getWorkflowWarnings()).contains("No attendance records have been recorded for this meeting.");
    }
}
