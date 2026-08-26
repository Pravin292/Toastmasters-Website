package com.rathinam.toastmasters.modules.attendance.repository;

import com.rathinam.toastmasters.modules.attendance.entity.AttendanceEntity;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {
    boolean existsByMeetingIdAndMemberId(UUID meetingId, UUID memberId);
    Optional<AttendanceEntity> findByMeetingIdAndMemberId(UUID meetingId, UUID memberId);
    List<AttendanceEntity> findByMeetingId(UUID meetingId);
    List<AttendanceEntity> findByMemberId(UUID memberId);
    long countByMemberIdAndStatus(UUID memberId, AttendanceStatus status);
    long countByMeetingId(UUID meetingId);
    long countByMeetingIdAndStatus(UUID meetingId, AttendanceStatus status);
    long countByMeetingMeetingStartBetween(LocalDateTime start, LocalDateTime end);
    long countByMeetingMeetingStartBetweenAndStatus(LocalDateTime start, LocalDateTime end, AttendanceStatus status);
    long countByMemberIdAndMeetingMeetingStartBetween(UUID memberId, LocalDateTime start, LocalDateTime end);
    long countByMemberIdAndMeetingMeetingStartBetweenAndStatus(UUID memberId, LocalDateTime start, LocalDateTime end, AttendanceStatus status);
}
