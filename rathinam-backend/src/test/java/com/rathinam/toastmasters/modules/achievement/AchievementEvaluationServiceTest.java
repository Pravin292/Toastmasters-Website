package com.rathinam.toastmasters.modules.achievement;

import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCriteriaType;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import com.rathinam.toastmasters.modules.achievement.entity.MemberAchievementEntity;
import com.rathinam.toastmasters.modules.achievement.repository.AchievementDefinitionRepository;
import com.rathinam.toastmasters.modules.achievement.repository.MemberAchievementRepository;
import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import com.rathinam.toastmasters.modules.attendance.repository.AttendanceRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementEvaluationServiceTest {

    @Mock
    private AchievementDefinitionRepository definitionRepository;

    @Mock
    private MemberAchievementRepository memberAchievementRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MeetingRoleAssignmentRepository roleAssignmentRepository;

    @Mock
    private PointEventRepository pointEventRepository;

    @InjectMocks
    private AchievementEvaluationService evaluationService;

    private UUID memberId;
    private MemberEntity member;
    private AchievementDefinitionEntity firstMeetingDef;
    private AchievementDefinitionEntity points100Def;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        member = new MemberEntity();
        member.setId(memberId);
        member.setDisplayName("Pravin");

        firstMeetingDef = new AchievementDefinitionEntity();
        firstMeetingDef.setId(UUID.randomUUID());
        firstMeetingDef.setCode("FIRST_MEETING");
        firstMeetingDef.setName("First Step");
        firstMeetingDef.setCategory(AchievementCategory.ATTENDANCE);
        firstMeetingDef.setCriteriaType(AchievementCriteriaType.ATTENDANCE_COUNT);
        firstMeetingDef.setCriteriaThreshold(1);
        firstMeetingDef.setActive(true);
        firstMeetingDef.setRepeatable(false);

        points100Def = new AchievementDefinitionEntity();
        points100Def.setId(UUID.randomUUID());
        points100Def.setCode("POINTS_100");
        points100Def.setName("Century Achiever");
        points100Def.setCategory(AchievementCategory.POINTS);
        points100Def.setCriteriaType(AchievementCriteriaType.TOTAL_POINTS);
        points100Def.setCriteriaThreshold(100);
        points100Def.setActive(true);
        points100Def.setRepeatable(false);
    }

    @Test
    void evaluateMemberAchievements_ThresholdMet_AwardsAchievement() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(definitionRepository.findByIsActiveTrue()).thenReturn(List.of(firstMeetingDef, points100Def));
        when(attendanceRepository.countByMemberIdAndStatus(memberId, AttendanceStatus.PRESENT)).thenReturn(1L);
        when(roleAssignmentRepository.countByMemberId(memberId)).thenReturn(0L);
        when(pointEventRepository.sumPointsByMemberId(memberId)).thenReturn(120);

        evaluationService.evaluateMemberAchievements(memberId);

        verify(memberAchievementRepository, org.mockito.Mockito.atLeastOnce()).save(any(MemberAchievementEntity.class));
    }

    @Test
    void evaluateMemberAchievements_AlreadyEarned_Skipped() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(definitionRepository.findByIsActiveTrue()).thenReturn(List.of(firstMeetingDef));
        when(attendanceRepository.countByMemberIdAndStatus(memberId, AttendanceStatus.PRESENT)).thenReturn(5L);
        when(memberAchievementRepository.existsByMemberIdAndAchievementDefinitionId(memberId, firstMeetingDef.getId())).thenReturn(true);

        evaluationService.evaluateMemberAchievements(memberId);

        verify(memberAchievementRepository, never()).save(any(MemberAchievementEntity.class));
    }
}
