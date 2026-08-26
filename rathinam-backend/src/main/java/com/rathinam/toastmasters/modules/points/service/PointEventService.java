package com.rathinam.toastmasters.modules.points.service;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.dto.LeaderboardEntryResponse;
import com.rathinam.toastmasters.modules.points.dto.MeetingPointsSummaryResponse;
import com.rathinam.toastmasters.modules.points.dto.MemberPointsSummaryResponse;
import com.rathinam.toastmasters.modules.points.dto.PointEventResponse;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import com.rathinam.toastmasters.modules.points.exception.PointEventNotFoundException;
import com.rathinam.toastmasters.modules.points.mapper.PointEventMapper;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PointEventService {

    private final PointEventRepository pointEventRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final PointEventMapper pointEventMapper;

    public PointEventService(PointEventRepository pointEventRepository,
                             MemberRepository memberRepository,
                             MeetingRepository meetingRepository,
                             PointEventMapper pointEventMapper) {
        this.pointEventRepository = pointEventRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.pointEventMapper = pointEventMapper;
    }

    @Transactional(readOnly = true)
    public PointEventResponse getPointEventById(UUID eventId) {
        PointEventEntity entity = pointEventRepository.findById(eventId)
                .orElseThrow(() -> new PointEventNotFoundException(eventId));
        return pointEventMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public MemberPointsSummaryResponse getMemberPointsSummary(UUID memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Integer totalPoints;
        Page<PointEventEntity> eventsPage;

        if (startDate != null && endDate != null) {
            totalPoints = pointEventRepository.sumPointsByMemberIdAndDateRange(memberId, startDate, endDate);
            eventsPage = pointEventRepository.findByMemberIdAndCreatedAtBetween(memberId, startDate, endDate, pageable);
        } else {
            totalPoints = pointEventRepository.sumPointsByMemberId(memberId);
            eventsPage = pointEventRepository.findByMemberId(memberId, pageable);
        }

        MemberPointsSummaryResponse response = new MemberPointsSummaryResponse();
        response.setMemberId(member.getId());
        response.setMemberDisplayName(member.getDisplayName());
        response.setMemberEmail(member.getEmail());
        response.setTotalPoints(totalPoints != null ? totalPoints : 0);
        response.setEvents(eventsPage.map(pointEventMapper::toResponse));
        return response;
    }

    @Transactional(readOnly = true)
    public MeetingPointsSummaryResponse getMeetingPointsSummary(UUID meetingId) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));

        List<PointEventEntity> events = pointEventRepository.findByMeetingId(meetingId);
        int totalPoints = events.stream().mapToInt(PointEventEntity::getPoints).sum();

        MeetingPointsSummaryResponse response = new MeetingPointsSummaryResponse();
        response.setMeetingId(meeting.getId());
        response.setMeetingNumber(meeting.getMeetingNumber());
        response.setTotalPointsAwarded(totalPoints);
        response.setEvents(events.stream().map(pointEventMapper::toResponse).collect(Collectors.toList()));
        return response;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboard(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now().plusYears(100);

        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        List<LeaderboardEntryResponse> leaderboard = pointEventRepository.findLeaderboardBetweenDates(start, end, pageable);

        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }
        return leaderboard;
    }
}
