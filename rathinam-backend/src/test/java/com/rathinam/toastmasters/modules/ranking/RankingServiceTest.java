package com.rathinam.toastmasters.modules.ranking;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import com.rathinam.toastmasters.modules.ranking.dto.LeaderboardResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MeetingRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MemberRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyPerformanceEntry;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse;
import com.rathinam.toastmasters.modules.ranking.exception.InvalidRankingPeriodException;
import com.rathinam.toastmasters.modules.ranking.service.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private PointEventRepository pointEventRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private RankingService rankingService;

    private UUID memberId1;
    private UUID memberId2;
    private UUID meetingId;
    private MemberEntity member1;
    private MemberEntity member2;
    private MeetingEntity meeting;

    @BeforeEach
    void setUp() {
        memberId1 = UUID.randomUUID();
        memberId2 = UUID.randomUUID();
        meetingId = UUID.randomUUID();

        member1 = new MemberEntity();
        member1.setId(memberId1);
        member1.setDisplayName("Pravin");
        member1.setEmail("pravin@test.com");

        member2 = new MemberEntity();
        member2.setId(memberId2);
        member2.setDisplayName("Member A");
        member2.setEmail("membera@test.com");

        meeting = new MeetingEntity();
        meeting.setId(meetingId);
        meeting.setMeetingNumber(25);
    }

    @Test
    void getLeaderboard_HighestPointRankedFirst_Success() {
        LocalDateTime now = LocalDateTime.now();
        RankingEntryResponse rank1 = new RankingEntryResponse(memberId1, "Pravin", "pravin@test.com", 150L);
        RankingEntryResponse rank2 = new RankingEntryResponse(memberId2, "Member A", "membera@test.com", 100L);

        when(pointEventRepository.findRankingsBetweenDates(any(), any(), any(Pageable.class)))
                .thenReturn(List.of(rank1, rank2));
        when(pointEventRepository.countDistinctMembersBetweenDates(any(), any())).thenReturn(2L);

        LeaderboardResponse response = rankingService.getLeaderboard(now.minusDays(30), now, PageRequest.of(0, 20));

        assertThat(response).isNotNull();
        assertThat(response.getEntries().getContent()).hasSize(2);
        assertThat(response.getEntries().getContent().get(0).getRank()).isEqualTo(1);
        assertThat(response.getEntries().getContent().get(0).getDisplayName()).isEqualTo("Pravin");
        assertThat(response.getEntries().getContent().get(0).getPoints()).isEqualTo(150L);
        assertThat(response.getEntries().getContent().get(1).getRank()).isEqualTo(2);
    }

    @Test
    void getLeaderboard_InvalidDateRange_ThrowsException() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(5);

        assertThatThrownBy(() -> rankingService.getLeaderboard(start, end, PageRequest.of(0, 20)))
                .isInstanceOf(InvalidRankingPeriodException.class);
    }

    @Test
    void getMonthlyRanking_ResolvesChampionCorrectly() {
        RankingEntryResponse championEntry = new RankingEntryResponse(memberId1, "Pravin", "pravin@test.com", 148L);

        when(pointEventRepository.findRankingsBetweenDates(any(), any(), any(Pageable.class)))
                .thenReturn(List.of(championEntry));
        when(pointEventRepository.countDistinctMembersBetweenDates(any(), any())).thenReturn(1L);

        MonthlyRankingResponse response = rankingService.getMonthlyRanking(2026, 8, PageRequest.of(0, 20));

        assertThat(response).isNotNull();
        assertThat(response.getYear()).isEqualTo(2026);
        assertThat(response.getMonth()).isEqualTo(8);
        assertThat(response.getChampion()).isNotNull();
        assertThat(response.getChampion().getDisplayName()).isEqualTo("Pravin");
        assertThat(response.getChampion().getPoints()).isEqualTo(148L);
    }

    @Test
    void getMonthlyRanking_EmptyMonth_HasNullChampion() {
        when(pointEventRepository.findRankingsBetweenDates(any(), any(), any(Pageable.class)))
                .thenReturn(List.of());
        when(pointEventRepository.countDistinctMembersBetweenDates(any(), any())).thenReturn(0L);

        MonthlyRankingResponse response = rankingService.getMonthlyRanking(2026, 7, PageRequest.of(0, 20));

        assertThat(response).isNotNull();
        assertThat(response.getLeaderboard().getContent()).isEmpty();
        assertThat(response.getChampion()).isNull();
    }

    @Test
    void getMemberRanking_ReturnsCorrectMemberRank() {
        RankingEntryResponse entry1 = new RankingEntryResponse(memberId2, "Member A", "membera@test.com", 200L);
        RankingEntryResponse entry2 = new RankingEntryResponse(memberId1, "Pravin", "pravin@test.com", 148L);

        when(memberRepository.findById(memberId1)).thenReturn(Optional.of(member1));
        when(pointEventRepository.findRankingsBetweenDates(any(), any(), any(Pageable.class)))
                .thenReturn(List.of(entry1, entry2));

        MemberRankingResponse response = rankingService.getMemberRanking(memberId1, 2026, 8);

        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(memberId1);
        assertThat(response.getRank()).isEqualTo(2);
        assertThat(response.getTotalPoints()).isEqualTo(148L);
    }

    @Test
    void getMeetingRankings_ReturnsRankedMembersForMeeting() {
        RankingEntryResponse r1 = new RankingEntryResponse(memberId1, "Pravin", "pravin@test.com", 20L);
        RankingEntryResponse r2 = new RankingEntryResponse(memberId2, "Member A", "membera@test.com", 15L);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        when(pointEventRepository.findMeetingRankings(meetingId)).thenReturn(List.of(r1, r2));

        MeetingRankingResponse response = rankingService.getMeetingRankings(meetingId);

        assertThat(response).isNotNull();
        assertThat(response.getMeetingNumber()).isEqualTo(25);
        assertThat(response.getRankings()).hasSize(2);
        assertThat(response.getRankings().get(0).getRank()).isEqualTo(1);
        assertThat(response.getRankings().get(0).getPoints()).isEqualTo(20L);
    }

    @Test
    void getMemberMonthlyPerformance_GroupsPointsByMonth() {
        PointEventEntity event1 = new PointEventEntity();
        event1.setPoints(10);
        event1.setCreatedAt(LocalDateTime.parse("2026-07-15T10:00:00"));

        PointEventEntity event2 = new PointEventEntity();
        event2.setPoints(15);
        event2.setCreatedAt(LocalDateTime.parse("2026-08-10T10:00:00"));

        when(memberRepository.existsById(memberId1)).thenReturn(true);
        when(pointEventRepository.findAllByMemberId(memberId1)).thenReturn(List.of(event1, event2));

        List<MonthlyPerformanceEntry> trends = rankingService.getMemberMonthlyPerformance(memberId1);

        assertThat(trends).hasSize(2);
        assertThat(trends.get(0).getYear()).isEqualTo(2026);
        assertThat(trends.get(0).getMonth()).isEqualTo(7);
        assertThat(trends.get(0).getPoints()).isEqualTo(10L);
        assertThat(trends.get(1).getMonth()).isEqualTo(8);
        assertThat(trends.get(1).getPoints()).isEqualTo(15L);
    }
}
