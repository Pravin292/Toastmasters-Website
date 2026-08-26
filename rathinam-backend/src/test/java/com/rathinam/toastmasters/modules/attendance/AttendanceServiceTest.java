package com.rathinam.toastmasters.modules.attendance;

import com.rathinam.toastmasters.modules.attendance.dto.AttendanceResponse;
import com.rathinam.toastmasters.modules.attendance.dto.CreateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.dto.UpdateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceEntity;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import com.rathinam.toastmasters.modules.attendance.exception.AttendanceNotFoundException;
import com.rathinam.toastmasters.modules.attendance.exception.DuplicateAttendanceException;
import com.rathinam.toastmasters.modules.attendance.mapper.AttendanceMapper;
import com.rathinam.toastmasters.modules.attendance.repository.AttendanceRepository;
import com.rathinam.toastmasters.modules.attendance.service.AttendanceService;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.points.service.PointAwardService;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PointAwardService pointAwardService;

    @Mock
    private AchievementEvaluationService achievementEvaluationService;

    @Spy
    private AttendanceMapper attendanceMapper;

    @InjectMocks
    private AttendanceService attendanceService;

    private UUID meetingId;
    private UUID memberId;
    private UUID attendanceId;
    private MeetingEntity meetingEntity;
    private MemberEntity memberEntity;
    private AttendanceEntity attendanceEntity;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        attendanceId = UUID.randomUUID();

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(101);

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setDisplayName("John Doe");
        memberEntity.setEmail("john.doe@example.com");

        attendanceEntity = new AttendanceEntity();
        attendanceEntity.setId(attendanceId);
        attendanceEntity.setMeeting(meetingEntity);
        attendanceEntity.setMember(memberEntity);
        attendanceEntity.setStatus(AttendanceStatus.PRESENT);
        attendanceEntity.setCheckInTime(OffsetDateTime.parse("2026-09-01T18:05:00+05:30"));
    }

    @Test
    void recordAttendance_Present_Success() {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.PRESENT);
        request.setCheckInTime(OffsetDateTime.parse("2026-09-01T18:05:00+05:30"));

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(attendanceRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(false);
        when(attendanceRepository.save(any(AttendanceEntity.class))).thenReturn(attendanceEntity);

        AttendanceResponse response = attendanceService.recordAttendance(meetingId, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(response.getMemberId()).isEqualTo(memberId);
        assertThat(response.getMeetingId()).isEqualTo(meetingId);
    }

    @Test
    void recordAttendance_Absent_Success() {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.ABSENT);
        attendanceEntity.setStatus(AttendanceStatus.ABSENT);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(attendanceRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(false);
        when(attendanceRepository.save(any(AttendanceEntity.class))).thenReturn(attendanceEntity);

        AttendanceResponse response = attendanceService.recordAttendance(meetingId, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
    }

    @Test
    void recordAttendance_Excused_Success() {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.EXCUSED);
        attendanceEntity.setStatus(AttendanceStatus.EXCUSED);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(attendanceRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(false);
        when(attendanceRepository.save(any(AttendanceEntity.class))).thenReturn(attendanceEntity);

        AttendanceResponse response = attendanceService.recordAttendance(meetingId, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AttendanceStatus.EXCUSED);
    }

    @Test
    void recordAttendance_Duplicate_ThrowsException() {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.PRESENT);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(attendanceRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(true);

        assertThatThrownBy(() -> attendanceService.recordAttendance(meetingId, request))
                .isInstanceOf(DuplicateAttendanceException.class);
    }

    @Test
    void recordAttendance_UnknownMeeting_ThrowsException() {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.PRESENT);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.recordAttendance(meetingId, request))
                .isInstanceOf(MeetingNotFoundException.class);
    }

    @Test
    void recordAttendance_UnknownMember_ThrowsException() {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.PRESENT);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.recordAttendance(meetingId, request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getMeetingAttendance_Success() {
        when(meetingRepository.existsById(meetingId)).thenReturn(true);
        when(attendanceRepository.findByMeetingId(meetingId)).thenReturn(List.of(attendanceEntity));

        List<AttendanceResponse> responses = attendanceService.getMeetingAttendance(meetingId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    }

    @Test
    void updateAttendance_Success() {
        UpdateAttendanceRequest request = new UpdateAttendanceRequest(AttendanceStatus.EXCUSED);

        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(attendanceEntity));
        when(attendanceRepository.save(any(AttendanceEntity.class))).thenAnswer(i -> i.getArgument(0));

        AttendanceResponse response = attendanceService.updateAttendance(attendanceId, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AttendanceStatus.EXCUSED);
    }

    @Test
    void getAttendanceById_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        when(attendanceRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.getAttendanceById(unknownId))
                .isInstanceOf(AttendanceNotFoundException.class);
    }
}
