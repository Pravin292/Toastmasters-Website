package com.rathinam.toastmasters.modules.meeting.repository;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeetingRepository extends JpaRepository<MeetingEntity, UUID> {
    boolean existsByMeetingNumber(Integer meetingNumber);
    Optional<MeetingEntity> findByMeetingNumber(Integer meetingNumber);
    long countByMeetingStartBetween(LocalDateTime start, LocalDateTime end);
    List<MeetingEntity> findByMeetingStartBetweenOrderByMeetingStartAsc(LocalDateTime start, LocalDateTime end);
}
