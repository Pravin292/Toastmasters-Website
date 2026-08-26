package com.rathinam.toastmasters.modules.ai;

import com.rathinam.toastmasters.modules.ai.dto.GenerateMeetingSummaryRequest;
import com.rathinam.toastmasters.modules.ai.dto.MeetingSummaryResponse;
import com.rathinam.toastmasters.modules.ai.provider.AiProvider;
import com.rathinam.toastmasters.modules.ai.service.AiMeetingSummaryService;
import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.service.AnalyticsService;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiMeetingSummaryServiceTest {

    @Mock
    private AiProvider aiProvider;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private MeetingRoleAssignmentRepository roleAssignmentRepository;

    @InjectMocks
    private AiMeetingSummaryService summaryService;

    private UUID meetingId;
    private MeetingEntity meetingEntity;
    private MeetingAnalyticsResponse analyticsResponse;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(101);
        meetingEntity.setTheme("Innovate & Lead");
        meetingEntity.setMeetingStart(OffsetDateTime.now());
        meetingEntity.setMeetingType(MeetingType.REGULAR);
        meetingEntity.setStatus(MeetingStatus.COMPLETED);

        analyticsResponse = new MeetingAnalyticsResponse();
        analyticsResponse.setMeetingId(meetingId);
        analyticsResponse.setMeetingNumber(101);
        analyticsResponse.setTotalAttendanceRecords(10);
        analyticsResponse.setPresentCount(8);
        analyticsResponse.setAbsentCount(1);
        analyticsResponse.setExcusedCount(1);
        analyticsResponse.setAttendancePercentage(80.0);
        analyticsResponse.setRolesAssigned(5);
        analyticsResponse.setRolesFilled(5);
        analyticsResponse.setRolesRemaining(0);
        analyticsResponse.setTotalPointsAwarded(150);
        analyticsResponse.setParticipatingMembersCount(8);
    }

    @Test
    void generateMeetingSummary_Success() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(analyticsService.getMeetingAnalytics(meetingId)).thenReturn(analyticsResponse);
        when(roleAssignmentRepository.findByMeetingId(meetingId)).thenReturn(List.of());
        when(aiProvider.generate(anyString())).thenReturn("Meeting #101 was highly successful with strong attendance.");
        when(aiProvider.getProviderName()).thenReturn("gemini");
        when(aiProvider.getModelName()).thenReturn("gemini-1.5-flash");

        GenerateMeetingSummaryRequest request = new GenerateMeetingSummaryRequest("Participation", "Encouraging");
        MeetingSummaryResponse response = summaryService.generateMeetingSummary(meetingId, request);

        assertThat(response).isNotNull();
        assertThat(response.getMeetingNumber()).isEqualTo(101);
        assertThat(response.getConciseSummary()).isEqualTo("Meeting #101 was highly successful with strong attendance.");
        assertThat(response.getAiProvider()).isEqualTo("gemini");
        assertThat(response.isAiGenerated()).isTrue();

        verify(aiProvider).generate(anyString());
    }

    @Test
    void generateMeetingSummary_MeetingNotFound_ThrowsException() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> summaryService.generateMeetingSummary(meetingId, null))
                .isInstanceOf(MeetingNotFoundException.class);
    }
}
