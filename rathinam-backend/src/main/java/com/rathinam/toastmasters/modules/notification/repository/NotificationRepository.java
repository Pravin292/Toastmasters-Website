package com.rathinam.toastmasters.modules.notification.repository;

import com.rathinam.toastmasters.modules.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    Page<NotificationEntity> findByMemberIdOrderByCreatedAtDesc(UUID memberId, Pageable pageable);

    Page<NotificationEntity> findByMemberIdAndReadAtIsNullOrderByCreatedAtDesc(UUID memberId, Pageable pageable);

    long countByMemberIdAndReadAtIsNull(UUID memberId);

    boolean existsBySourceTypeAndSourceIdAndMemberId(String sourceType, UUID sourceId, UUID memberId);

    Optional<NotificationEntity> findByIdAndMemberId(UUID id, UUID memberId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.readAt = :readAt WHERE n.member.id = :memberId AND n.readAt IS NULL")
    int markAllAsReadForMember(@Param("memberId") UUID memberId, @Param("readAt") OffsetDateTime readAt);
}
