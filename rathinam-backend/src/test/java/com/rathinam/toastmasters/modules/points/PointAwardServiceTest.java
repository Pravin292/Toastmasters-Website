package com.rathinam.toastmasters.modules.points;

import com.rathinam.toastmasters.modules.attendance.entity.AttendanceEntity;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.dto.ManualPointAdjustmentRequest;
import com.rathinam.toastmasters.modules.points.dto.PointEventResponse;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import com.rathinam.toastmasters.modules.points.entity.PointRuleCategory;
import com.rathinam.toastmasters.modules.points.entity.PointRuleEntity;
import com.rathinam.toastmasters.modules.points.mapper.PointEventMapper;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import com.rathinam.toastmasters.modules.points.repository.PointRuleRepository;
import com.rathinam.toastmasters.modules.points.service.PointAwardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;

@ExtendWith(MockitoExtension.class)
class PointAwardServiceTest {

    @Mock
    private PointEventRepository pointEventRepository;

    @Mock
    private PointRuleRepository pointRuleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private AchievementEvaluationService achievementEvaluationService;

    @Spy
    private PointEventMapper pointEventMapper;

    @InjectMocks
    private PointAwardService pointAwardService;

    private UUID memberId;
    private UUID meetingId;
    private UUID attendanceId;
    private UUID roleAssignmentId;
    private UUID roleDefId;
    private MemberEntity memberEntity;
    private MeetingEntity meetingEntity;
    private AttendanceEntity attendanceEntity;
    private RoleDefinitionEntity roleDefEntity;
    private MeetingRoleAssignmentEntity roleAssignmentEntity;
    private PointRuleEntity attendanceRule;
    private PointRuleEntity roleRule;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        meetingId = UUID.randomUUID();
        attendanceId = UUID.randomUUID();
        roleAssignmentId = UUID.randomUUID();
        roleDefId = UUID.randomUUID();

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setDisplayName("Pravin");

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(25);

        attendanceEntity = new AttendanceEntity();
        attendanceEntity.setId(attendanceId);
        attendanceEntity.setMember(memberEntity);
        attendanceEntity.setMeeting(meetingEntity);
        attendanceEntity.setStatus(AttendanceStatus.PRESENT);

        roleDefEntity = new RoleDefinitionEntity();
        roleDefEntity.setId(roleDefId);
        roleDefEntity.setName("Toastmaster of the Day");
        roleDefEntity.setActive(true);

        roleAssignmentEntity = new MeetingRoleAssignmentEntity();
        roleAssignmentEntity.setId(roleAssignmentId);
        roleAssignmentEntity.setMember(memberEntity);
        roleAssignmentEntity.setMeeting(meetingEntity);
        roleAssignmentEntity.setRoleDefinition(roleDefEntity);

        attendanceRule = new PointRuleEntity();
        attendanceRule.setCode("ATTENDANCE_PRESENT");
        attendanceRule.setName("Attendance Present");
        attendanceRule.setPoints(5);
        attendanceRule.setActive(true);
        attendanceRule.setCategory(PointRuleCategory.ATTENDANCE);

        roleRule = new PointRuleEntity();
        roleRule.setCode("ROLE_TOASTMASTER");
        roleRule.setName("Role: Toastmaster");
        roleRule.setPoints(10);
        roleRule.setActive(true);
        roleRule.setCategory(PointRuleCategory.ROLE);
        roleRule.setRoleDefinition(roleDefEntity);
    }

    @Test
    void awardPointsForAttendance_Present_Success() {
        when(pointRuleRepository.findByCodeIgnoreCase("ATTENDANCE_PRESENT")).thenReturn(Optional.of(attendanceRule));
        when(pointEventRepository.existsBySourceTypeAndSourceId("ATTENDANCE", attendanceId)).thenReturn(false);
        when(pointEventRepository.save(any(PointEventEntity.class))).thenAnswer(i -> {
            PointEventEntity e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        Optional<PointEventResponse> response = pointAwardService.awardPointsForAttendance(attendanceEntity);

        assertThat(response).isPresent();
        assertThat(response.get().getPoints()).isEqualTo(5);
        assertThat(response.get().getSourceType()).isEqualTo("ATTENDANCE");
    }

    @Test
    void awardPointsForAttendance_Absent_NoEventCreated() {
        attendanceEntity.setStatus(AttendanceStatus.ABSENT);

        Optional<PointEventResponse> response = pointAwardService.awardPointsForAttendance(attendanceEntity);

        assertThat(response).isEmpty();
    }

    @Test
    void awardPointsForAttendance_Idempotent_SecondCallSkipped() {
        when(pointRuleRepository.findByCodeIgnoreCase("ATTENDANCE_PRESENT")).thenReturn(Optional.of(attendanceRule));
        when(pointEventRepository.existsBySourceTypeAndSourceId("ATTENDANCE", attendanceId)).thenReturn(true);

        Optional<PointEventResponse> response = pointAwardService.awardPointsForAttendance(attendanceEntity);

        assertThat(response).isEmpty();
    }

    @Test
    void awardPointsForMeetingRole_Success() {
        when(pointRuleRepository.findByRoleDefinitionIdAndActiveTrue(roleDefId)).thenReturn(Optional.of(roleRule));
        when(pointEventRepository.existsBySourceTypeAndSourceId("MEETING_ROLE", roleAssignmentId)).thenReturn(false);
        when(pointEventRepository.save(any(PointEventEntity.class))).thenAnswer(i -> {
            PointEventEntity e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        Optional<PointEventResponse> response = pointAwardService.awardPointsForMeetingRole(roleAssignmentEntity);

        assertThat(response).isPresent();
        assertThat(response.get().getPoints()).isEqualTo(10);
    }

    @Test
    void awardPointsForMeetingRole_InactiveRule_Skipped() {
        roleRule.setActive(false);
        when(pointRuleRepository.findByRoleDefinitionIdAndActiveTrue(roleDefId)).thenReturn(Optional.empty());

        Optional<PointEventResponse> response = pointAwardService.awardPointsForMeetingRole(roleAssignmentEntity);

        assertThat(response).isEmpty();
    }

    @Test
    void awardManualPoints_Positive_Success() {
        ManualPointAdjustmentRequest request = new ManualPointAdjustmentRequest(memberId, 5, "Special Contribution");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(pointRuleRepository.findByCodeIgnoreCase("MANUAL_BONUS")).thenReturn(Optional.empty());
        when(pointEventRepository.save(any(PointEventEntity.class))).thenAnswer(i -> {
            PointEventEntity e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        PointEventResponse response = pointAwardService.awardManualPoints(request);

        assertThat(response).isNotNull();
        assertThat(response.getPoints()).isEqualTo(5);
        assertThat(response.getReason()).isEqualTo("Special Contribution");
    }

    @Test
    void awardManualPoints_NegativeCorrection_Success() {
        ManualPointAdjustmentRequest request = new ManualPointAdjustmentRequest(memberId, -10, "Correction for error");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(pointRuleRepository.findByCodeIgnoreCase("MANUAL_BONUS")).thenReturn(Optional.empty());
        when(pointEventRepository.save(any(PointEventEntity.class))).thenAnswer(i -> {
            PointEventEntity e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        PointEventResponse response = pointAwardService.awardManualPoints(request);

        assertThat(response).isNotNull();
        assertThat(response.getPoints()).isEqualTo(-10);
        assertThat(response.getReason()).isEqualTo("Correction for error");
    }
}
