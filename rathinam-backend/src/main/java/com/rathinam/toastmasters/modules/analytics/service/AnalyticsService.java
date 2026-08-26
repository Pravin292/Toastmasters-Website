package com.rathinam.toastmasters.modules.analytics.service;

import com.rathinam.toastmasters.modules.achievement.repository.MemberAchievementRepository;
import com.rathinam.toastmasters.modules.analytics.dto.ClubOverviewAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MemberAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MemberMonthlyPerformanceResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MonthlyAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MonthlyReportResponse;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import com.rathinam.toastmasters.modules.attendance.repository.AttendanceRepository;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberStatus;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import com.rathinam.toastmasters.modules.ranking.dto.MemberRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyChampionResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse;
import com.rathinam.toastmasters.modules.ranking.exception.InvalidRankingPeriodException;
import com.rathinam.toastmasters.modules.ranking.service.ChampionshipService;
import com.rathinam.toastmasters.modules.ranking.service.RankingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final AttendanceRepository attendanceRepository;
    private final MeetingRoleAssignmentRepository roleAssignmentRepository;
    private final RoleDefinitionRepository roleDefinitionRepository;
    private final PointEventRepository pointEventRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final RankingService rankingService;
    private final ChampionshipService championshipService;

    public AnalyticsService(MemberRepository memberRepository,
                            MeetingRepository meetingRepository,
                            AttendanceRepository attendanceRepository,
                            MeetingRoleAssignmentRepository roleAssignmentRepository,
                            RoleDefinitionRepository roleDefinitionRepository,
                            PointEventRepository pointEventRepository,
                            MemberAchievementRepository memberAchievementRepository,
                            RankingService rankingService,
                            ChampionshipService championshipService) {
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.attendanceRepository = attendanceRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.roleDefinitionRepository = roleDefinitionRepository;
        this.pointEventRepository = pointEventRepository;
        this.memberAchievementRepository = memberAchievementRepository;
        this.rankingService = rankingService;
        this.championshipService = championshipService;
    }

    public MemberAnalyticsResponse getMemberAnalytics(UUID memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        long totalMeetingsAttended = attendanceRepository.countByMemberIdAndStatus(memberId, AttendanceStatus.PRESENT);
        long totalClubMeetings = meetingRepository.count();
        double attendancePercentage = totalClubMeetings > 0 ? (totalMeetingsAttended * 100.0 / totalClubMeetings) : 0.0;

        long totalRolesPerformed = roleAssignmentRepository.countByMemberId(memberId);
        Integer totalPointsInt = pointEventRepository.sumPointsByMemberId(memberId);
        int totalPoints = totalPointsInt != null ? totalPointsInt : 0;

        YearMonth currentPeriod = YearMonth.now();
        Integer currentRank = null;
        try {
            MemberRankingResponse rankResponse = rankingService.getMemberRanking(memberId, currentPeriod.getYear(), currentPeriod.getMonthValue());
            if (rankResponse != null && rankResponse.getRank() != null && rankResponse.getRank() > 0) {
                currentRank = rankResponse.getRank();
            }
        } catch (Exception ignored) {
            // Rank remains null if member has 0 points or not ranked
        }

        long achievementsEarned = memberAchievementRepository.countByMemberId(memberId);

        return new MemberAnalyticsResponse(
                member.getId(),
                member.getDisplayName(),
                member.getEmail(),
                totalMeetingsAttended,
                Math.round(attendancePercentage * 100.0) / 100.0,
                totalRolesPerformed,
                totalPoints,
                currentRank,
                achievementsEarned
        );
    }

    public MeetingAnalyticsResponse getMeetingAnalytics(UUID meetingId) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));

        long totalAttendanceRecords = attendanceRepository.countByMeetingId(meetingId);
        long presentCount = attendanceRepository.countByMeetingIdAndStatus(meetingId, AttendanceStatus.PRESENT);
        long absentCount = attendanceRepository.countByMeetingIdAndStatus(meetingId, AttendanceStatus.ABSENT);
        long excusedCount = attendanceRepository.countByMeetingIdAndStatus(meetingId, AttendanceStatus.EXCUSED);

        long activeMembers = memberRepository.countByStatus(MemberStatus.ACTIVE);
        double attendancePercentage = activeMembers > 0
                ? (presentCount * 100.0 / activeMembers)
                : (totalAttendanceRecords > 0 ? (presentCount * 100.0 / totalAttendanceRecords) : 0.0);

        long rolesAssigned = roleAssignmentRepository.countByMeetingId(meetingId);
        long rolesFilled = rolesAssigned;
        long totalActiveRoles = roleDefinitionRepository.countByActiveTrue();
        long rolesRemaining = Math.max(0, totalActiveRoles - rolesFilled);

        Integer totalPointsInt = pointEventRepository.sumPointsByMeetingId(meetingId);
        int totalPointsAwarded = totalPointsInt != null ? totalPointsInt : 0;

        long participatingMembers = Math.max(presentCount, rolesFilled);

        MeetingAnalyticsResponse response = new MeetingAnalyticsResponse();
        response.setMeetingId(meeting.getId());
        response.setMeetingNumber(meeting.getMeetingNumber());
        response.setMeetingStart(meeting.getMeetingStart());
        response.setTheme(meeting.getTheme());
        response.setMeetingType(meeting.getMeetingType());
        response.setStatus(meeting.getStatus());
        response.setTotalAttendanceRecords(totalAttendanceRecords);
        response.setPresentCount(presentCount);
        response.setAbsentCount(absentCount);
        response.setExcusedCount(excusedCount);
        response.setAttendancePercentage(Math.round(attendancePercentage * 100.0) / 100.0);
        response.setRolesAssigned(rolesAssigned);
        response.setRolesFilled(rolesFilled);
        response.setRolesRemaining(rolesRemaining);
        response.setTotalPointsAwarded(totalPointsAwarded);
        response.setParticipatingMembersCount(participatingMembers);

        return response;
    }

    public ClubOverviewAnalyticsResponse getClubOverviewAnalytics() {
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(MemberStatus.ACTIVE);
        long totalMeetings = meetingRepository.count();
        long totalAttendanceRecords = attendanceRepository.count();
        double averageAttendancePerMeeting = totalMeetings > 0 ? ((double) totalAttendanceRecords / totalMeetings) : 0.0;

        Integer totalPointsInt = pointEventRepository.sumTotalPoints();
        int totalPointsAwarded = totalPointsInt != null ? totalPointsInt : 0;

        long totalAchievementsEarned = memberAchievementRepository.count();

        MonthlyChampionResponse champion = null;
        try {
            MonthlyRankingResponse championshipResponse = championshipService.getCurrentMonthlyChampionship();
            if (championshipResponse != null) {
                champion = championshipResponse.getChampion();
            }
        } catch (Exception ignored) {
        }

        return new ClubOverviewAnalyticsResponse(
                totalMembers,
                activeMembers,
                totalMeetings,
                totalAttendanceRecords,
                Math.round(averageAttendancePerMeeting * 100.0) / 100.0,
                totalPointsAwarded,
                totalAchievementsEarned,
                champion
        );
    }

    public MonthlyAnalyticsResponse getMonthlyAnalytics(int year, int month) {
        YearMonth period = validateAndGetYearMonth(year, month);

        LocalDateTime start = period.atDay(1).atStartOfDay();
        LocalDateTime end = period.atEndOfMonth().atTime(23, 59, 59, 999999999);

        long totalMeetings = meetingRepository.countByMeetingStartBetween(start, end);
        long totalAttendance = attendanceRepository.countByMeetingMeetingStartBetweenAndStatus(start, end, AttendanceStatus.PRESENT);
        double averageAttendance = totalMeetings > 0 ? ((double) totalAttendance / totalMeetings) : 0.0;

        Integer totalPointsInt = pointEventRepository.sumPointsBetweenDates(start, end);
        int totalPointsAwarded = totalPointsInt != null ? totalPointsInt : 0;

        long activeMembersCount = memberRepository.countByStatus(MemberStatus.ACTIVE);

        List<RankingEntryResponse> topPerformers = new ArrayList<>();
        try {
            MonthlyRankingResponse rankingResponse = rankingService.getMonthlyRanking(year, month, PageRequest.of(0, 5));
            if (rankingResponse != null && rankingResponse.getLeaderboard() != null) {
                topPerformers = rankingResponse.getLeaderboard().getContent();
            }
        } catch (Exception ignored) {
        }

        MonthlyChampionResponse champion = null;
        try {
            MonthlyRankingResponse championshipResponse = championshipService.getMonthlyChampionship(year, month);
            if (championshipResponse != null) {
                champion = championshipResponse.getChampion();
            }
        } catch (Exception ignored) {
        }

        OffsetDateTime startOffset = start.atOffset(ZoneOffset.UTC);
        OffsetDateTime endOffset = end.atOffset(ZoneOffset.UTC);
        long achievementsEarned = memberAchievementRepository.countByEarnedAtBetween(startOffset, endOffset);

        return new MonthlyAnalyticsResponse(
                year,
                month,
                totalMeetings,
                totalAttendance,
                Math.round(averageAttendance * 100.0) / 100.0,
                totalPointsAwarded,
                activeMembersCount,
                topPerformers,
                champion,
                achievementsEarned
        );
    }

    public List<MemberMonthlyPerformanceResponse> getMemberPerformanceTrend(UUID memberId, Integer monthsParam) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }

        int months = monthsParam != null && monthsParam > 0 ? monthsParam : 6;
        YearMonth currentMonth = YearMonth.now();
        List<MemberMonthlyPerformanceResponse> trend = new ArrayList<>();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth period = currentMonth.minusMonths(i);
            LocalDateTime start = period.atDay(1).atStartOfDay();
            LocalDateTime end = period.atEndOfMonth().atTime(23, 59, 59, 999999999);

            Integer pointsInt = pointEventRepository.sumPointsByMemberIdAndDateRange(memberId, start, end);
            int points = pointsInt != null ? pointsInt : 0;

            long attendanceCount = attendanceRepository.countByMemberIdAndMeetingMeetingStartBetweenAndStatus(memberId, start, end, AttendanceStatus.PRESENT);
            long rolesCount = roleAssignmentRepository.countByMemberIdAndMeetingMeetingStartBetween(memberId, start, end);

            trend.add(new MemberMonthlyPerformanceResponse(period.getYear(), period.getMonthValue(), points, attendanceCount, rolesCount));
        }

        return trend;
    }

    public MonthlyReportResponse generateMonthlyReport(int year, int month) {
        YearMonth period = validateAndGetYearMonth(year, month);

        MonthlyAnalyticsResponse monthlyData = getMonthlyAnalytics(year, month);

        LocalDateTime start = period.atDay(1).atStartOfDay();
        LocalDateTime end = period.atEndOfMonth().atTime(23, 59, 59, 999999999);

        long totalAttendanceRecords = attendanceRepository.countByMeetingMeetingStartBetween(start, end);
        long presentCount = attendanceRepository.countByMeetingMeetingStartBetweenAndStatus(start, end, AttendanceStatus.PRESENT);
        long absentCount = attendanceRepository.countByMeetingMeetingStartBetweenAndStatus(start, end, AttendanceStatus.ABSENT);
        long excusedCount = attendanceRepository.countByMeetingMeetingStartBetweenAndStatus(start, end, AttendanceStatus.EXCUSED);

        String periodString = period.getMonth().name() + " " + period.getYear();

        MonthlyReportResponse.MeetingReportStats meetingStats = new MonthlyReportResponse.MeetingReportStats(
                monthlyData.getTotalMeetings(),
                monthlyData.getAverageAttendance()
        );

        MonthlyReportResponse.AttendanceReportStats attendanceStats = new MonthlyReportResponse.AttendanceReportStats(
                totalAttendanceRecords,
                presentCount,
                absentCount,
                excusedCount
        );

        MonthlyReportResponse.PointsReportStats pointsStats = new MonthlyReportResponse.PointsReportStats(
                monthlyData.getTotalPointsAwarded()
        );

        return new MonthlyReportResponse(
                periodString,
                year,
                month,
                meetingStats,
                attendanceStats,
                pointsStats,
                monthlyData.getTopPerformers(),
                monthlyData.getMonthlyChampion(),
                monthlyData.getAchievementsEarned()
        );
    }

    private YearMonth validateAndGetYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException ex) {
            throw new InvalidRankingPeriodException("Invalid year or month: " + year + "-" + month);
        }
    }
}
