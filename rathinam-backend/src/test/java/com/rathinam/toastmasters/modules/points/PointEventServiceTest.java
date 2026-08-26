package com.rathinam.toastmasters.modules.points;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.dto.LeaderboardEntryResponse;
import com.rathinam.toastmasters.modules.points.dto.MeetingPointsSummaryResponse;
import com.rathinam.toastmasters.modules.points.dto.MemberPointsSummaryResponse;
import com.rathinam.toastmasters.modules.points.dto.PointEventResponse;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import com.rathinam.toastmasters.modules.points.exception.PointEventNotFoundException;
import com.rathinam.toastmasters.modules.points.mapper.PointEventMapper;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import com.rathinam.toastmasters.modules.points.service.PointEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointEventServiceTest {

    @Mock
    private PointEventRepository pointEventRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Spy
    private PointEventMapper pointEventMapper;

    @InjectMocks
    private PointEventService pointEventService;

    private UUID memberId;
    private UUID meetingId;
    private UUID eventId;
    private MemberEntity memberEntity;
    private MeetingEntity meetingEntity;
    private PointEventEntity eventEntity;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        meetingId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setDisplayName("Pravin");

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(25);

        eventEntity = new PointEventEntity();
        eventEntity.setId(eventId);
        eventEntity.setMember(memberEntity);
        eventEntity.setMeeting(meetingEntity);
        eventEntity.setPoints(10);
        eventEntity.setReason("Toastmaster of the Day");
        eventEntity.setSourceType("MEETING_ROLE");
    }

    @Test
    void getPointEventById_Success() {
        when(pointEventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));

        PointEventResponse response = pointEventService.getPointEventById(eventId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(eventId);
        assertThat(response.getPoints()).isEqualTo(10);
    }

    @Test
    void getPointEventById_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        when(pointEventRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointEventService.getPointEventById(unknownId))
                .isInstanceOf(PointEventNotFoundException.class);
    }

    @Test
    void getMemberPointsSummary_CalculatesTotalDynamically() {
        Pageable pageable = PageRequest.of(0, 10);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(pointEventRepository.sumPointsByMemberId(memberId)).thenReturn(15);
        when(pointEventRepository.findByMemberId(eq(memberId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(eventEntity)));

        MemberPointsSummaryResponse summary = pointEventService.getMemberPointsSummary(memberId, null, null, pageable);

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalPoints()).isEqualTo(15);
        assertThat(summary.getEvents().getContent()).hasSize(1);
    }

    @Test
    void getMeetingPointsSummary_CalculatesMeetingTotal() {
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));
        when(pointEventRepository.findByMeetingId(meetingId)).thenReturn(List.of(eventEntity));

        MeetingPointsSummaryResponse summary = pointEventService.getMeetingPointsSummary(meetingId);

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalPointsAwarded()).isEqualTo(10);
        assertThat(summary.getEvents()).hasSize(1);
    }

    @Test
    void getLeaderboard_ReturnsOrderedRanks() {
        LeaderboardEntryResponse entry1 = new LeaderboardEntryResponse(memberId, "Pravin", "pravin@test.com", 25L);
        LeaderboardEntryResponse entry2 = new LeaderboardEntryResponse(UUID.randomUUID(), "Alice", "alice@test.com", 15L);

        when(pointEventRepository.findLeaderboardBetweenDates(any(), any(), any(Pageable.class)))
                .thenReturn(List.of(entry1, entry2));

        List<LeaderboardEntryResponse> leaderboard = pointEventService.getLeaderboard(null, null, 10);

        assertThat(leaderboard).hasSize(2);
        assertThat(leaderboard.get(0).getRank()).isEqualTo(1);
        assertThat(leaderboard.get(0).getTotalPoints()).isEqualTo(25L);
        assertThat(leaderboard.get(1).getRank()).isEqualTo(2);
        assertThat(leaderboard.get(1).getTotalPoints()).isEqualTo(15L);
    }
}
