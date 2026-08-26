package com.rathinam.toastmasters.modules.meetingrole;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleAssignmentRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleAssignmentResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.UpdateRoleAssignmentRequest;
import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateMeetingRoleAssignmentException;
import com.rathinam.toastmasters.modules.meetingrole.exception.InactiveRoleDefinitionException;
import com.rathinam.toastmasters.modules.meetingrole.exception.MeetingRoleAssignmentNotFoundException;
import com.rathinam.toastmasters.modules.meetingrole.exception.RoleDefinitionNotFoundException;
import com.rathinam.toastmasters.modules.meetingrole.mapper.MeetingRoleAssignmentMapper;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import com.rathinam.toastmasters.modules.meetingrole.service.MeetingRoleAssignmentService;
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
class MeetingRoleAssignmentServiceTest {

    @Mock
    private MeetingRoleAssignmentRepository assignmentRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private RoleDefinitionRepository roleDefinitionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PointAwardService pointAwardService;

    @Mock
    private AchievementEvaluationService achievementEvaluationService;

    @Mock
    private com.rathinam.toastmasters.modules.notification.service.NotificationService notificationService;

    @Spy
    private MeetingRoleAssignmentMapper assignmentMapper;

    @InjectMocks
    private MeetingRoleAssignmentService assignmentService;

    private UUID meetingId;
    private UUID roleDefId;
    private UUID memberId;
    private UUID assignmentId;
    private MeetingEntity meetingEntity;
    private RoleDefinitionEntity roleDefEntity;
    private MemberEntity memberEntity;
    private MeetingRoleAssignmentEntity assignmentEntity;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        roleDefId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        assignmentId = UUID.randomUUID();

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(101);

        roleDefEntity = new RoleDefinitionEntity();
        roleDefEntity.setId(roleDefId);
        roleDefEntity.setName("Timer");
        roleDefEntity.setActive(true);

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setDisplayName("John Doe");

        assignmentEntity = new MeetingRoleAssignmentEntity();
        assignmentEntity.setId(assignmentId);
        assignmentEntity.setMeeting(meetingEntity);
        assignmentEntity.setRoleDefinition(roleDefEntity);
        assignmentEntity.setMember(memberEntity);
    }

    @Test
    void assignRole_Success() {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(roleDefinitionRepository.findById(roleDefId)).thenReturn(Optional.of(roleDefEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(assignmentRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(false);
        when(assignmentRepository.existsByMeetingIdAndRoleDefinitionId(meetingId, roleDefId)).thenReturn(false);
        when(assignmentRepository.save(any(MeetingRoleAssignmentEntity.class))).thenReturn(assignmentEntity);

        RoleAssignmentResponse response = assignmentService.assignRole(meetingId, request);

        assertThat(response).isNotNull();
        assertThat(response.getRoleName()).isEqualTo("Timer");
        assertThat(response.getMemberDisplayName()).isEqualTo("John Doe");
    }

    @Test
    void assignRole_UnknownMeeting_ThrowsException() {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.assignRole(meetingId, request))
                .isInstanceOf(MeetingNotFoundException.class);
    }

    @Test
    void assignRole_UnknownMember_ThrowsException() {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(roleDefinitionRepository.findById(roleDefId)).thenReturn(Optional.of(roleDefEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.assignRole(meetingId, request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void assignRole_UnknownRole_ThrowsException() {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(roleDefinitionRepository.findById(roleDefId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.assignRole(meetingId, request))
                .isInstanceOf(RoleDefinitionNotFoundException.class);
    }

    @Test
    void assignRole_InactiveRole_ThrowsException() {
        roleDefEntity.setActive(false);
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(roleDefinitionRepository.findById(roleDefId)).thenReturn(Optional.of(roleDefEntity));

        assertThatThrownBy(() -> assignmentService.assignRole(meetingId, request))
                .isInstanceOf(InactiveRoleDefinitionException.class);
    }

    @Test
    void assignRole_MemberAlreadyHasRole_ThrowsException() {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(roleDefinitionRepository.findById(roleDefId)).thenReturn(Optional.of(roleDefEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(assignmentRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(true);

        assertThatThrownBy(() -> assignmentService.assignRole(meetingId, request))
                .isInstanceOf(DuplicateMeetingRoleAssignmentException.class)
                .hasMessageContaining("Member already has a role assigned");
    }

    @Test
    void assignRole_RoleAlreadyAssignedInMeeting_ThrowsException() {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(roleDefinitionRepository.findById(roleDefId)).thenReturn(Optional.of(roleDefEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(assignmentRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(false);
        when(assignmentRepository.existsByMeetingIdAndRoleDefinitionId(meetingId, roleDefId)).thenReturn(true);

        assertThatThrownBy(() -> assignmentService.assignRole(meetingId, request))
                .isInstanceOf(DuplicateMeetingRoleAssignmentException.class)
                .hasMessageContaining("Role 'Timer' is already assigned");
    }

    @Test
    void getMeetingRoleAssignments_Success() {
        when(meetingRepository.existsById(meetingId)).thenReturn(true);
        when(assignmentRepository.findByMeetingId(meetingId)).thenReturn(List.of(assignmentEntity));

        List<RoleAssignmentResponse> responses = assignmentService.getMeetingRoleAssignments(meetingId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRoleName()).isEqualTo("Timer");
    }

    @Test
    void updateAssignment_Success() {
        UUID newRoleDefId = UUID.randomUUID();
        RoleDefinitionEntity newRoleDef = new RoleDefinitionEntity();
        newRoleDef.setId(newRoleDefId);
        newRoleDef.setName("Grammarian");
        newRoleDef.setActive(true);

        UpdateRoleAssignmentRequest request = new UpdateRoleAssignmentRequest();
        request.setRoleDefinitionId(newRoleDefId);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignmentEntity));
        when(roleDefinitionRepository.findById(newRoleDefId)).thenReturn(Optional.of(newRoleDef));
        when(assignmentRepository.existsByMeetingIdAndRoleDefinitionId(meetingId, newRoleDefId)).thenReturn(false);
        when(assignmentRepository.save(any(MeetingRoleAssignmentEntity.class))).thenAnswer(i -> i.getArgument(0));

        RoleAssignmentResponse response = assignmentService.updateAssignment(assignmentId, request);

        assertThat(response).isNotNull();
        assertThat(response.getRoleName()).isEqualTo("Grammarian");
    }
}
