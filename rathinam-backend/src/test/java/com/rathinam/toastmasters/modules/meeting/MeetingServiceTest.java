package com.rathinam.toastmasters.modules.meeting;

import com.rathinam.toastmasters.modules.meeting.dto.CreateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingResponse;
import com.rathinam.toastmasters.modules.meeting.dto.UpdateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;
import com.rathinam.toastmasters.modules.meeting.exception.DuplicateMeetingNumberException;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Spy
    private MeetingMapper meetingMapper;

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
        meetingEntity.setMeetingStart(OffsetDateTime.parse("2026-09-01T18:00:00+05:30"));
        meetingEntity.setTheme("Embracing Change");
        meetingEntity.setMeetingType(MeetingType.REGULAR);
        meetingEntity.setStatus(MeetingStatus.SCHEDULED);
        meetingEntity.setLocation("Auditorium Hall A");
    }

    @Test
    void createMeeting_Success() {
        CreateMeetingRequest request = new CreateMeetingRequest(101, OffsetDateTime.parse("2026-09-01T18:00:00+05:30"), MeetingType.REGULAR);
        request.setTheme("Embracing Change");

        when(meetingRepository.existsByMeetingNumber(101)).thenReturn(false);
        when(meetingRepository.save(any(MeetingEntity.class))).thenReturn(meetingEntity);

        MeetingResponse response = meetingService.createMeeting(request);

        assertThat(response).isNotNull();
        assertThat(response.getMeetingNumber()).isEqualTo(101);
        assertThat(response.getTheme()).isEqualTo("Embracing Change");
        assertThat(response.getStatus()).isEqualTo(MeetingStatus.SCHEDULED);
    }

    @Test
    void createMeeting_DuplicateNumber_ThrowsException() {
        CreateMeetingRequest request = new CreateMeetingRequest(101, OffsetDateTime.parse("2026-09-01T18:00:00+05:30"), MeetingType.REGULAR);
        when(meetingRepository.existsByMeetingNumber(101)).thenReturn(true);

        assertThatThrownBy(() -> meetingService.createMeeting(request))
                .isInstanceOf(DuplicateMeetingNumberException.class)
                .hasMessageContaining("#101");
    }

    @Test
    void getMeetingById_Success() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));

        MeetingResponse response = meetingService.getMeetingById(meetingId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(meetingId);
        assertThat(response.getMeetingNumber()).isEqualTo(101);
    }

    @Test
    void getMeetingById_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        when(meetingRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.getMeetingById(unknownId))
                .isInstanceOf(MeetingNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    @Test
    void getMeetings_Paginated_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<MeetingEntity> page = new PageImpl<>(List.of(meetingEntity));
        when(meetingRepository.findAll(pageable)).thenReturn(page);

        Page<MeetingResponse> result = meetingService.getMeetings(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMeetingNumber()).isEqualTo(101);
    }

    @Test
    void updateMeeting_Success() {
        UpdateMeetingRequest updateRequest = new UpdateMeetingRequest();
        updateRequest.setTheme("Updated Theme");
        updateRequest.setStatus(MeetingStatus.IN_PROGRESS);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(meetingRepository.save(any(MeetingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MeetingResponse response = meetingService.updateMeeting(meetingId, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTheme()).isEqualTo("Updated Theme");
        assertThat(response.getStatus()).isEqualTo(MeetingStatus.IN_PROGRESS);
    }
}
