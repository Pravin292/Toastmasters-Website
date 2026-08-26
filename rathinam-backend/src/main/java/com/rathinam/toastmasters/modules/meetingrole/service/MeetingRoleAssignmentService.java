package com.rathinam.toastmasters.modules.meetingrole.service;

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
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.points.service.PointAwardService;

import com.rathinam.toastmasters.modules.notification.entity.NotificationType;
import com.rathinam.toastmasters.modules.notification.service.NotificationService;

@Service
public class MeetingRoleAssignmentService {

    private final MeetingRoleAssignmentRepository assignmentRepository;
    private final MeetingRepository meetingRepository;
    private final RoleDefinitionRepository roleDefinitionRepository;
    private final MemberRepository memberRepository;
    private final MeetingRoleAssignmentMapper assignmentMapper;
    private final PointAwardService pointAwardService;
    private final AchievementEvaluationService achievementEvaluationService;
    private final NotificationService notificationService;

    public MeetingRoleAssignmentService(MeetingRoleAssignmentRepository assignmentRepository,
                                        MeetingRepository meetingRepository,
                                        RoleDefinitionRepository roleDefinitionRepository,
                                        MemberRepository memberRepository,
                                        MeetingRoleAssignmentMapper assignmentMapper,
                                        PointAwardService pointAwardService,
                                        AchievementEvaluationService achievementEvaluationService,
                                        NotificationService notificationService) {
        this.assignmentRepository = assignmentRepository;
        this.meetingRepository = meetingRepository;
        this.roleDefinitionRepository = roleDefinitionRepository;
        this.memberRepository = memberRepository;
        this.assignmentMapper = assignmentMapper;
        this.pointAwardService = pointAwardService;
        this.achievementEvaluationService = achievementEvaluationService;
        this.notificationService = notificationService;
    }

    @Transactional
    public RoleAssignmentResponse assignRole(UUID meetingId, CreateRoleAssignmentRequest request) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));

        RoleDefinitionEntity roleDef = roleDefinitionRepository.findById(request.getRoleDefinitionId())
                .orElseThrow(() -> new RoleDefinitionNotFoundException(request.getRoleDefinitionId()));

        if (!roleDef.isActive()) {
            throw new InactiveRoleDefinitionException(request.getRoleDefinitionId());
        }

        MemberEntity member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));

        // Rule #1: Member can perform only ONE role in a meeting
        if (assignmentRepository.existsByMeetingIdAndMemberId(meetingId, request.getMemberId())) {
            throw new DuplicateMeetingRoleAssignmentException("Member already has a role assigned for this meeting");
        }

        // Rule #2: Specific role can be assigned only ONCE per meeting
        if (assignmentRepository.existsByMeetingIdAndRoleDefinitionId(meetingId, request.getRoleDefinitionId())) {
            throw new DuplicateMeetingRoleAssignmentException("Role '" + roleDef.getName() + "' is already assigned for this meeting");
        }

        MeetingRoleAssignmentEntity entity = assignmentMapper.toEntity(request, meeting, roleDef, member);
        MeetingRoleAssignmentEntity saved = assignmentRepository.save(entity);

        // Automatically award points for meeting role
        pointAwardService.awardPointsForMeetingRole(saved);

        // Evaluate achievements
        achievementEvaluationService.evaluateMemberAchievements(saved.getMember().getId());

        // Notify assigned member
        notificationService.notifyMember(
                saved.getMember().getId(),
                NotificationType.ROLE_ASSIGNMENT,
                "Role Assigned: " + saved.getRoleDefinition().getName(),
                String.format("You have been assigned as %s for Meeting #%d.", saved.getRoleDefinition().getName(), saved.getMeeting().getMeetingNumber()),
                saved.getMeeting().getId(),
                "MEETING_ROLE",
                saved.getId()
        );

        return assignmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RoleAssignmentResponse> getMeetingRoleAssignments(UUID meetingId) {
        if (!meetingRepository.existsById(meetingId)) {
            throw new MeetingNotFoundException(meetingId);
        }

        return assignmentRepository.findByMeetingId(meetingId).stream()
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleAssignmentResponse getAssignmentById(UUID assignmentId) {
        MeetingRoleAssignmentEntity entity = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new MeetingRoleAssignmentNotFoundException(assignmentId));
        return assignmentMapper.toResponse(entity);
    }

    @Transactional
    public RoleAssignmentResponse updateAssignment(UUID assignmentId, UpdateRoleAssignmentRequest request) {
        MeetingRoleAssignmentEntity entity = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new MeetingRoleAssignmentNotFoundException(assignmentId));

        UUID meetingId = entity.getMeeting().getId();

        if (request.getMemberId() != null && !request.getMemberId().equals(entity.getMember().getId())) {
            MemberEntity newMember = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));
            if (assignmentRepository.existsByMeetingIdAndMemberId(meetingId, request.getMemberId())) {
                throw new DuplicateMeetingRoleAssignmentException("Member already has a role assigned for this meeting");
            }
            entity.setMember(newMember);
        }

        if (request.getRoleDefinitionId() != null && !request.getRoleDefinitionId().equals(entity.getRoleDefinition().getId())) {
            RoleDefinitionEntity newRole = roleDefinitionRepository.findById(request.getRoleDefinitionId())
                    .orElseThrow(() -> new RoleDefinitionNotFoundException(request.getRoleDefinitionId()));
            if (!newRole.isActive()) {
                throw new InactiveRoleDefinitionException(request.getRoleDefinitionId());
            }
            if (assignmentRepository.existsByMeetingIdAndRoleDefinitionId(meetingId, request.getRoleDefinitionId())) {
                throw new DuplicateMeetingRoleAssignmentException("Role '" + newRole.getName() + "' is already assigned for this meeting");
            }
            entity.setRoleDefinition(newRole);
        }

        MeetingRoleAssignmentEntity updated = assignmentRepository.save(entity);
        return assignmentMapper.toResponse(updated);
    }
}
