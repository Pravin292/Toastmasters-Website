package com.rathinam.toastmasters.modules.notification;

import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleAssignmentRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleAssignmentResponse;
import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.meetingrole.mapper.MeetingRoleAssignmentMapper;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import com.rathinam.toastmasters.modules.meetingrole.service.MeetingRoleAssignmentService;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.notification.entity.NotificationType;
import com.rathinam.toastmasters.modules.notification.service.NotificationService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationIntegrationTest {

    @Mock
    private MeetingRoleAssignmentRepository assignmentRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private RoleDefinitionRepository roleDefinitionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private MeetingRoleAssignmentMapper assignmentMapper;

    @Mock
    private PointAwardService pointAwardService;

    @Mock
    private AchievementEvaluationService achievementEvaluationService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MeetingRoleAssignmentService assignmentService;

    private UUID meetingId;
    private UUID memberId;
    private UUID roleDefinitionId;

    private MeetingEntity meetingEntity;
    private MemberEntity memberEntity;
    private RoleDefinitionEntity roleDefinitionEntity;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        roleDefinitionId = UUID.randomUUID();

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(105);

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setEmail("assigned@toastmasters.com");

        roleDefinitionEntity = new RoleDefinitionEntity();
        roleDefinitionEntity.setId(roleDefinitionId);
        roleDefinitionEntity.setName("Timer");
        roleDefinitionEntity.setActive(true);
    }

    @Test
    void assignRole_TriggersRoleAssignmentNotification() {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest();
        request.setMemberId(memberId);
        request.setRoleDefinitionId(roleDefinitionId);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(roleDefinitionRepository.findById(roleDefinitionId)).thenReturn(Optional.of(roleDefinitionEntity));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(assignmentRepository.existsByMeetingIdAndMemberId(meetingId, memberId)).thenReturn(false);
        when(assignmentRepository.existsByMeetingIdAndRoleDefinitionId(meetingId, roleDefinitionId)).thenReturn(false);
        when(assignmentRepository.save(any(MeetingRoleAssignmentEntity.class))).thenAnswer(invocation -> {
            MeetingRoleAssignmentEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        RoleAssignmentResponse response = assignmentService.assignRole(meetingId, request);

        assertThat(response).isNotNull();

        verify(notificationService).notifyMember(
                eq(memberId),
                eq(NotificationType.ROLE_ASSIGNMENT),
                eq("Role Assigned: Timer"),
                eq("You have been assigned as Timer for Meeting #105."),
                eq(meetingId),
                eq("MEETING_ROLE"),
                any(UUID.class)
        );
    }
}
