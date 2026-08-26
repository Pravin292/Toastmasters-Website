package com.rathinam.toastmasters.modules.analytics;

import com.rathinam.toastmasters.modules.achievement.repository.MemberAchievementRepository;
import com.rathinam.toastmasters.modules.analytics.dto.ClubOverviewAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MemberAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MemberMonthlyPerformanceResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MonthlyReportResponse;
import com.rathinam.toastmasters.modules.analytics.service.AnalyticsService;
import com.rathinam.toastmasters.modules.attendance.repository.AttendanceRepository;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import com.rathinam.toastmasters.modules.ranking.exception.InvalidRankingPeriodException;
import com.rathinam.toastmasters.modules.ranking.service.ChampionshipService;
import com.rathinam.toastmasters.modules.ranking.service.RankingService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MeetingRoleAssignmentRepository roleAssignmentRepository;

    @Mock
    private RoleDefinitionRepository roleDefinitionRepository;

    @Mock
    private PointEventRepository pointEventRepository;

    @Mock
    private MemberAchievementRepository memberAchievementRepository;

    @Mock
    private RankingService rankingService;

    @Mock
    private ChampionshipService championshipService;

    @InjectMocks
    private AnalyticsService analyticsService;

    private UUID memberId;
    private UUID meetingId;
    private MemberEntity memberEntity;
    private MeetingEntity meetingEntity;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        meetingId = UUID.randomUUID();

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setDisplayName("Pravin");
        memberEntity.setEmail("pravin@test.com");

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(101);
        meetingEntity.setMeetingStart(OffsetDateTime.now());
        meetingEntity.setMeetingType(MeetingType.REGULAR);
        meetingEntity.setStatus(MeetingStatus.COMPLETED);
    }

    @Test
    void getMemberAnalytics_Success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(attendanceRepository.countByMemberIdAndStatus(memberId, com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus.PRESENT)).thenReturn(5L);
        when(meetingRepository.count()).thenReturn(10L);
        when(roleAssignmentRepository.countByMemberId(memberId)).thenReturn(3L);
        when(pointEventRepository.sumPointsByMemberId(memberId)).thenReturn(50);
        when(memberAchievementRepository.countByMemberId(memberId)).thenReturn(2L);

        MemberAnalyticsResponse response = analyticsService.getMemberAnalytics(memberId);

        assertThat(response).isNotNull();
        assertThat(response.getDisplayName()).isEqualTo("Pravin");
        assertThat(response.getTotalMeetingsAttended()).isEqualTo(5L);
        assertThat(response.getAttendancePercentage()).isEqualTo(50.0);
        assertThat(response.getTotalPoints()).isEqualTo(50);
    }

    @Test
    void getMemberAnalytics_NotFound_ThrowsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> analyticsService.getMemberAnalytics(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getMeetingAnalytics_Success() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(attendanceRepository.countByMeetingId(meetingId)).thenReturn(8L);
        when(attendanceRepository.countByMeetingIdAndStatus(meetingId, com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus.PRESENT)).thenReturn(6L);
        when(roleAssignmentRepository.countByMeetingId(meetingId)).thenReturn(5L);
        when(roleDefinitionRepository.countByActiveTrue()).thenReturn(10L);

        MeetingAnalyticsResponse response = analyticsService.getMeetingAnalytics(meetingId);

        assertThat(response).isNotNull();
        assertThat(response.getMeetingNumber()).isEqualTo(101);
        assertThat(response.getPresentCount()).isEqualTo(6L);
        assertThat(response.getRolesRemaining()).isEqualTo(5L);
    }

    @Test
    void getMeetingAnalytics_NotFound_ThrowsException() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> analyticsService.getMeetingAnalytics(meetingId))
                .isInstanceOf(MeetingNotFoundException.class);
    }

    @Test
    void getClubOverviewAnalytics_Success() {
        when(memberRepository.count()).thenReturn(20L);
        when(meetingRepository.count()).thenReturn(15L);
        when(attendanceRepository.count()).thenReturn(150L);
        when(pointEventRepository.sumTotalPoints()).thenReturn(1200);

        ClubOverviewAnalyticsResponse response = analyticsService.getClubOverviewAnalytics();

        assertThat(response).isNotNull();
        assertThat(response.getTotalMembers()).isEqualTo(20L);
        assertThat(response.getTotalPointsAwarded()).isEqualTo(1200);
    }

    @Test
    void getMonthlyAnalytics_InvalidMonth_ThrowsException() {
        assertThatThrownBy(() -> analyticsService.getMonthlyAnalytics(2026, 13))
                .isInstanceOf(InvalidRankingPeriodException.class);
    }

    @Test
    void getMemberPerformanceTrend_Success() {
        when(memberRepository.existsById(memberId)).thenReturn(true);

        List<MemberMonthlyPerformanceResponse> trend = analyticsService.getMemberPerformanceTrend(memberId, 3);

        assertThat(trend).hasSize(3);
    }

    @Test
    void generateMonthlyReport_Success() {
        MonthlyReportResponse response = analyticsService.generateMonthlyReport(2026, 8);

        assertThat(response).isNotNull();
        assertThat(response.getYear()).isEqualTo(2026);
        assertThat(response.getMonth()).isEqualTo(8);
        assertThat(response.getReportingPeriod()).isEqualTo("AUGUST 2026");
    }
}
