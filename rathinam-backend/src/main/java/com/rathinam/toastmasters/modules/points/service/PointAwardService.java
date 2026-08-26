package com.rathinam.toastmasters.modules.points.service;

import com.rathinam.toastmasters.modules.attendance.entity.AttendanceEntity;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.dto.ManualPointAdjustmentRequest;
import com.rathinam.toastmasters.modules.points.dto.PointEventResponse;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import com.rathinam.toastmasters.modules.points.entity.PointRuleEntity;
import com.rathinam.toastmasters.modules.points.mapper.PointEventMapper;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import com.rathinam.toastmasters.modules.points.repository.PointRuleRepository;
import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PointAwardService {

    private final PointEventRepository pointEventRepository;
    private final PointRuleRepository pointRuleRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final PointEventMapper pointEventMapper;
    private final AchievementEvaluationService achievementEvaluationService;

    public PointAwardService(PointEventRepository pointEventRepository,
                             PointRuleRepository pointRuleRepository,
                             MemberRepository memberRepository,
                             MeetingRepository meetingRepository,
                             PointEventMapper pointEventMapper,
                             AchievementEvaluationService achievementEvaluationService) {
        this.pointEventRepository = pointEventRepository;
        this.pointRuleRepository = pointRuleRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.pointEventMapper = pointEventMapper;
        this.achievementEvaluationService = achievementEvaluationService;
    }

    @Transactional
    public Optional<PointEventResponse> awardPointsForAttendance(AttendanceEntity attendance) {
        if (attendance == null || attendance.getStatus() == null) {
            return Optional.empty();
        }

        String ruleCode = "ATTENDANCE_" + attendance.getStatus().name();
        Optional<PointRuleEntity> ruleOpt = pointRuleRepository.findByCodeIgnoreCase(ruleCode);

        if (ruleOpt.isEmpty() || !ruleOpt.get().isActive()) {
            return Optional.empty();
        }

        PointRuleEntity rule = ruleOpt.get();

        // Do not create event for zero points unless PRESENT
        if (rule.getPoints() <= 0 && attendance.getStatus() != AttendanceStatus.PRESENT) {
            return Optional.empty();
        }

        String sourceType = "ATTENDANCE";
        UUID sourceId = attendance.getId();

        if (pointEventRepository.existsBySourceTypeAndSourceId(sourceType, sourceId)) {
            return Optional.empty(); // Idempotency check
        }

        PointEventEntity event = new PointEventEntity();
        event.setMember(attendance.getMember());
        event.setMeeting(attendance.getMeeting());
        event.setPointRule(rule);
        event.setPoints(rule.getPoints()); // Snapshot point value
        event.setReason("Attendance: " + attendance.getStatus().name() + " for Meeting #" + attendance.getMeeting().getMeetingNumber());
        event.setSourceType(sourceType);
        event.setSourceId(sourceId);

        try {
            PointEventEntity saved = pointEventRepository.save(event);
            return Optional.of(pointEventMapper.toResponse(saved));
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<PointEventResponse> awardPointsForMeetingRole(MeetingRoleAssignmentEntity assignment) {
        if (assignment == null || assignment.getRoleDefinition() == null) {
            return Optional.empty();
        }

        UUID roleDefId = assignment.getRoleDefinition().getId();
        Optional<PointRuleEntity> ruleOpt = pointRuleRepository.findByRoleDefinitionIdAndActiveTrue(roleDefId);

        if (ruleOpt.isEmpty()) {
            String fallbackCode = "ROLE_" + assignment.getRoleDefinition().getName().replaceAll("\\s+", "_").toUpperCase();
            ruleOpt = pointRuleRepository.findByCodeIgnoreCase(fallbackCode);
        }

        if (ruleOpt.isEmpty() || !ruleOpt.get().isActive()) {
            return Optional.empty();
        }

        PointRuleEntity rule = ruleOpt.get();
        String sourceType = "MEETING_ROLE";
        UUID sourceId = assignment.getId();

        if (pointEventRepository.existsBySourceTypeAndSourceId(sourceType, sourceId)) {
            return Optional.empty(); // Idempotency check
        }

        PointEventEntity event = new PointEventEntity();
        event.setMember(assignment.getMember());
        event.setMeeting(assignment.getMeeting());
        event.setPointRule(rule);
        event.setPoints(rule.getPoints()); // Snapshot point value
        event.setReason("Meeting Role: " + assignment.getRoleDefinition().getName() + " in Meeting #" + assignment.getMeeting().getMeetingNumber());
        event.setSourceType(sourceType);
        event.setSourceId(sourceId);

        try {
            PointEventEntity saved = pointEventRepository.save(event);
            return Optional.of(pointEventMapper.toResponse(saved));
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public PointEventResponse awardManualPoints(ManualPointAdjustmentRequest request) {
        MemberEntity member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));

        MeetingEntity meeting = null;
        if (request.getMeetingId() != null) {
            meeting = meetingRepository.findById(request.getMeetingId())
                    .orElseThrow(() -> new MeetingNotFoundException(request.getMeetingId()));
        }

        PointRuleEntity manualRule = pointRuleRepository.findByCodeIgnoreCase("MANUAL_BONUS").orElse(null);

        PointEventEntity event = new PointEventEntity();
        event.setMember(member);
        event.setMeeting(meeting);
        event.setPointRule(manualRule);
        event.setPoints(request.getPoints()); // Positive or negative
        event.setReason(request.getReason().trim());
        event.setSourceType("MANUAL");
        event.setSourceId(UUID.randomUUID());

        PointEventEntity saved = pointEventRepository.save(event);
        return pointEventMapper.toResponse(saved);
    }
}
