package com.rathinam.toastmasters.modules.meetingrole.repository;

import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeetingRoleAssignmentRepository extends JpaRepository<MeetingRoleAssignmentEntity, UUID> {
    boolean existsByMeetingIdAndMemberId(UUID meetingId, UUID memberId);
    boolean existsByMeetingIdAndRoleDefinitionId(UUID meetingId, UUID roleDefinitionId);
    Optional<MeetingRoleAssignmentEntity> findByMeetingIdAndMemberId(UUID meetingId, UUID memberId);
    Optional<MeetingRoleAssignmentEntity> findByMeetingIdAndRoleDefinitionId(UUID meetingId, UUID roleDefinitionId);
    List<MeetingRoleAssignmentEntity> findByMeetingId(UUID meetingId);
    List<MeetingRoleAssignmentEntity> findByMemberId(UUID memberId);
    long countByMemberId(UUID memberId);
    long countByMeetingId(UUID meetingId);
    long countByMemberIdAndMeetingMeetingStartBetween(UUID memberId, LocalDateTime start, LocalDateTime end);
}
