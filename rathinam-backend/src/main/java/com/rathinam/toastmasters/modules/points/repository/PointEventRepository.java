package com.rathinam.toastmasters.modules.points.repository;

import com.rathinam.toastmasters.modules.points.dto.LeaderboardEntryResponse;
import com.rathinam.toastmasters.modules.points.entity.PointEventEntity;
import com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PointEventRepository extends JpaRepository<PointEventEntity, UUID> {

    boolean existsBySourceTypeAndSourceId(String sourceType, UUID sourceId);

    Page<PointEventEntity> findByMemberId(UUID memberId, Pageable pageable);

    Page<PointEventEntity> findByMemberIdAndCreatedAtBetween(UUID memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<PointEventEntity> findByMeetingId(UUID meetingId);

    @Query("SELECT COALESCE(SUM(e.points), 0) FROM PointEventEntity e WHERE e.member.id = :memberId")
    Integer sumPointsByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT COALESCE(SUM(e.points), 0) FROM PointEventEntity e")
    Integer sumTotalPoints();

    @Query("SELECT COALESCE(SUM(e.points), 0) FROM PointEventEntity e WHERE e.member.id = :memberId AND e.createdAt BETWEEN :startDate AND :endDate")
    Integer sumPointsByMemberIdAndDateRange(@Param("memberId") UUID memberId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(e.points), 0) FROM PointEventEntity e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    Integer sumPointsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(e.points), 0) FROM PointEventEntity e WHERE e.member.id = :memberId AND e.meeting.id = :meetingId")
    Integer sumPointsByMemberIdAndMeetingId(@Param("memberId") UUID memberId, @Param("meetingId") UUID meetingId);

    @Query("SELECT COALESCE(SUM(e.points), 0) FROM PointEventEntity e WHERE e.meeting.id = :meetingId")
    Integer sumPointsByMeetingId(@Param("meetingId") UUID meetingId);

    @Query("SELECT new com.rathinam.toastmasters.modules.points.dto.LeaderboardEntryResponse(" +
           "m.id, m.displayName, m.email, COALESCE(SUM(e.points), 0L)) " +
           "FROM PointEventEntity e JOIN e.member m " +
           "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY m.id, m.displayName, m.email " +
           "ORDER BY SUM(e.points) DESC")
    List<LeaderboardEntryResponse> findLeaderboardBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT new com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse(" +
           "m.id, m.displayName, m.email, COALESCE(SUM(e.points), 0L)) " +
           "FROM PointEventEntity e JOIN e.member m " +
           "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY m.id, m.displayName, m.email " +
           "HAVING SUM(e.points) > 0 " +
           "ORDER BY SUM(e.points) DESC, m.displayName ASC, m.id ASC")
    List<RankingEntryResponse> findRankingsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT e.member.id) " +
           "FROM PointEventEntity e " +
           "WHERE e.createdAt BETWEEN :startDate AND :endDate")
    Long countDistinctMembersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.rathinam.toastmasters.modules.ranking.dto.RankingEntryResponse(" +
           "m.id, m.displayName, m.email, COALESCE(SUM(e.points), 0L)) " +
           "FROM PointEventEntity e JOIN e.member m " +
           "WHERE e.meeting.id = :meetingId " +
           "GROUP BY m.id, m.displayName, m.email " +
           "ORDER BY SUM(e.points) DESC, m.displayName ASC, m.id ASC")
    List<RankingEntryResponse> findMeetingRankings(@Param("meetingId") UUID meetingId);

    @Query("SELECT e FROM PointEventEntity e WHERE e.member.id = :memberId")
    List<PointEventEntity> findAllByMemberId(@Param("memberId") UUID memberId);
}
