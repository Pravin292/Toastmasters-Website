package com.rathinam.toastmasters.modules.ranking.service;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import com.rathinam.toastmasters.modules.points.repository.PointEventRepository;
import com.rathinam.toastmasters.modules.ranking.dto.LeaderboardResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MeetingRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MemberRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyChampionResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyPerformanceEntry;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse;
import com.rathinam.toastmasters.modules.ranking.exception.InvalidRankingPeriodException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RankingService {

    private static final int MAX_PAGE_SIZE = 100;

    private final PointEventRepository pointEventRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;

    public RankingService(PointEventRepository pointEventRepository,
                          MemberRepository memberRepository,
                          MeetingRepository meetingRepository) {
        this.pointEventRepository = pointEventRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
    }

    public LeaderboardResponse getLeaderboard(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime effectiveStart = startDate != null ? startDate : now.minusDays(30);
        LocalDateTime effectiveEnd = endDate != null ? endDate : now;

        if (effectiveStart.isAfter(effectiveEnd)) {
            throw new InvalidRankingPeriodException("Start date cannot be after end date");
        }

        Pageable boundedPageable = sanitizePageable(pageable);
        List<RankingEntryResponse> entries = pointEventRepository.findRankingsBetweenDates(effectiveStart, effectiveEnd, boundedPageable);

        long offset = boundedPageable.getOffset();
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank((int) (offset + i + 1));
        }

        Long totalElements = pointEventRepository.countDistinctMembersBetweenDates(effectiveStart, effectiveEnd);
        Page<RankingEntryResponse> page = new PageImpl<>(entries, boundedPageable, totalElements != null ? totalElements : 0L);

        return new LeaderboardResponse(effectiveStart, effectiveEnd, page);
    }

    public MonthlyRankingResponse getMonthlyRanking(int year, int month, Pageable pageable) {
        validateYearMonth(year, month);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        Pageable boundedPageable = sanitizePageable(pageable);
        List<RankingEntryResponse> entries = pointEventRepository.findRankingsBetweenDates(startDate, endDate, boundedPageable);

        long offset = boundedPageable.getOffset();
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank((int) (offset + i + 1));
        }

        Long totalMembers = pointEventRepository.countDistinctMembersBetweenDates(startDate, endDate);
        Page<RankingEntryResponse> page = new PageImpl<>(entries, boundedPageable, totalMembers != null ? totalMembers : 0L);

        MonthlyChampionResponse champion = resolveMonthlyChampion(year, month, startDate, endDate);

        return new MonthlyRankingResponse(year, month, totalMembers != null ? totalMembers : 0L, page, champion);
    }

    public MemberRankingResponse getMemberRanking(UUID memberId, Integer year, Integer month) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        YearMonth targetPeriod = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        validateYearMonth(targetPeriod.getYear(), targetPeriod.getMonthValue());

        LocalDateTime startDate = targetPeriod.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetPeriod.atEndOfMonth().atTime(LocalTime.MAX);

        List<RankingEntryResponse> allRankings = pointEventRepository.findRankingsBetweenDates(startDate, endDate, PageRequest.of(0, 1000));

        Integer rank = null;
        Long points = 0L;

        for (int i = 0; i < allRankings.size(); i++) {
            RankingEntryResponse entry = allRankings.get(i);
            if (entry.getMemberId().equals(memberId)) {
                rank = i + 1;
                points = entry.getPoints();
                break;
            }
        }

        return new MemberRankingResponse(member.getId(), member.getDisplayName(), points, rank, targetPeriod.getYear(), targetPeriod.getMonthValue());
    }

    public MeetingRankingResponse getMeetingRankings(UUID meetingId) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));

        List<RankingEntryResponse> rankings = pointEventRepository.findMeetingRankings(meetingId);
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return new MeetingRankingResponse(meeting.getId(), meeting.getMeetingNumber(), rankings);
    }

    public List<MonthlyPerformanceEntry> getMemberMonthlyPerformance(UUID memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }

        List<PointEventEntity> events = pointEventRepository.findAllByMemberId(memberId);

        Map<YearMonth, Long> monthlyTotals = events.stream()
                .collect(Collectors.groupingBy(
                        e -> YearMonth.from(e.getCreatedAt()),
                        Collectors.summingLong(e -> e.getPoints() != null ? e.getPoints().longValue() : 0L)
                ));

        List<MonthlyPerformanceEntry> performanceEntries = new ArrayList<>();
        monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> performanceEntries.add(new MonthlyPerformanceEntry(
                        entry.getKey().getYear(),
                        entry.getKey().getMonthValue(),
                        entry.getValue()
                )));

        return performanceEntries;
    }

    private MonthlyChampionResponse resolveMonthlyChampion(int year, int month, LocalDateTime startDate, LocalDateTime endDate) {
        List<RankingEntryResponse> topRanked = pointEventRepository.findRankingsBetweenDates(startDate, endDate, PageRequest.of(0, 1));
        if (topRanked.isEmpty() || topRanked.get(0).getPoints() <= 0) {
            return null;
        }

        RankingEntryResponse winner = topRanked.get(0);
        return new MonthlyChampionResponse(year, month, winner.getMemberId(), winner.getDisplayName(), winner.getEmail(), winner.getPoints());
    }

    private Pageable sanitizePageable(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, 20);
        }
        int pageSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), pageSize);
    }

    private void validateYearMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new InvalidRankingPeriodException("Invalid month: " + month + ". Must be between 1 and 12.");
        }
        if (year < 2000 || year > 2100) {
            throw new InvalidRankingPeriodException("Invalid year: " + year + ". Must be between 2000 and 2100.");
        }
    }
}
