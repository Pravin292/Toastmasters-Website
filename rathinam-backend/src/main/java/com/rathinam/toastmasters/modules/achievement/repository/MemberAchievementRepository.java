package com.rathinam.toastmasters.modules.achievement.repository;

import com.rathinam.toastmasters.modules.achievement.entity.MemberAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberAchievementRepository extends JpaRepository<MemberAchievementEntity, UUID> {
    boolean existsByMemberIdAndAchievementDefinitionId(UUID memberId, UUID achievementDefinitionId);
    boolean existsByMemberIdAndAchievementDefinitionIdAndMeetingId(UUID memberId, UUID achievementDefinitionId, UUID meetingId);
    Optional<MemberAchievementEntity> findByMemberIdAndAchievementDefinitionId(UUID memberId, UUID achievementDefinitionId);
    List<MemberAchievementEntity> findByMemberId(UUID memberId);
    long countByMemberId(UUID memberId);
    long countByEarnedAtBetween(OffsetDateTime start, OffsetDateTime end);
}
