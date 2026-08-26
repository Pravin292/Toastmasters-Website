package com.rathinam.toastmasters.modules.achievement.service;

import com.rathinam.toastmasters.modules.achievement.entity.AchievementCriteriaType;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import com.rathinam.toastmasters.modules.achievement.entity.MemberAchievementEntity;
import com.rathinam.toastmasters.modules.achievement.repository.AchievementDefinitionRepository;
import com.rathinam.toastmasters.modules.achievement.repository.MemberAchievementRepository;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import com.rathinam.toastmasters.modules.attendance.repository.AttendanceRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AchievementEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(AchievementEvaluationService.class);

    private final AchievementDefinitionRepository definitionRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final MeetingRoleAssignmentRepository roleAssignmentRepository;
    private final PointEventRepository pointEventRepository;

    public AchievementEvaluationService(AchievementDefinitionRepository definitionRepository,
                                        MemberAchievementRepository memberAchievementRepository,
                                        MemberRepository memberRepository,
                                        AttendanceRepository attendanceRepository,
                                        MeetingRoleAssignmentRepository roleAssignmentRepository,
                                        PointEventRepository pointEventRepository) {
        this.definitionRepository = definitionRepository;
        this.memberAchievementRepository = memberAchievementRepository;
        this.memberRepository = memberRepository;
        this.attendanceRepository = attendanceRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.pointEventRepository = pointEventRepository;
    }

    public void evaluateMemberAchievements(UUID memberId) {
        MemberEntity member = memberRepository.findById(memberId).orElse(null);
        if (member == null) return;

        List<AchievementDefinitionEntity> activeDefinitions = definitionRepository.findByIsActiveTrue();
        if (activeDefinitions.isEmpty()) return;

        long attendanceCount = attendanceRepository.countByMemberIdAndStatus(memberId, AttendanceStatus.PRESENT);
        long roleCount = roleAssignmentRepository.countByMemberId(memberId);
        Integer totalPointsInt = pointEventRepository.sumPointsByMemberId(memberId);
        long totalPoints = totalPointsInt != null ? totalPointsInt.longValue() : 0L;

        for (AchievementDefinitionEntity def : activeDefinitions) {
            if (Boolean.FALSE.equals(def.getRepeatable()) &&
                memberAchievementRepository.existsByMemberIdAndAchievementDefinitionId(memberId, def.getId())) {
                continue;
            }

            boolean thresholdMet = false;
            AchievementCriteriaType criteriaType = def.getCriteriaType();
            Integer threshold = def.getCriteriaThreshold();

            if (criteriaType == AchievementCriteriaType.ATTENDANCE_COUNT && threshold != null) {
                thresholdMet = attendanceCount >= threshold;
            } else if (criteriaType == AchievementCriteriaType.ROLE_COUNT && threshold != null) {
                thresholdMet = roleCount >= threshold;
            } else if (criteriaType == AchievementCriteriaType.TOTAL_POINTS && threshold != null) {
                thresholdMet = totalPoints >= threshold;
            }

            if (thresholdMet) {
                awardAchievementIdempotently(member, def, null, "Achieved " + def.getName());
            }
        }
    }

    public void awardMonthlyChampionAchievement(UUID memberId, String reason) {
        MemberEntity member = memberRepository.findById(memberId).orElse(null);
        if (member == null) return;

        AchievementDefinitionEntity championDef = definitionRepository.findByCodeIgnoreCase("MONTHLY_CHAMPION")
                .orElse(null);
        if (championDef == null || Boolean.FALSE.equals(championDef.getActive())) return;

        awardAchievementIdempotently(member, championDef, null, reason != null ? reason : "Monthly Champion");
    }

    private void awardAchievementIdempotently(MemberEntity member, AchievementDefinitionEntity def, UUID meetingId, String reason) {
        MemberAchievementEntity entity = new MemberAchievementEntity();
        entity.setMember(member);
        entity.setAchievementDefinition(def);
        entity.setEarnedAt(OffsetDateTime.now());
        entity.setReason(reason);

        try {
            memberAchievementRepository.save(entity);
            log.info("Awarded achievement '{}' to member '{}'", def.getCode(), member.getDisplayName());
        } catch (DataIntegrityViolationException ex) {
            log.debug("Achievement '{}' already awarded to member '{}'", def.getCode(), member.getId());
        }
    }
}
