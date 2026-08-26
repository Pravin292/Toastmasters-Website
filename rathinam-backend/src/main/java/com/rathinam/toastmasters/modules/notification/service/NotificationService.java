package com.rathinam.toastmasters.modules.notification.service;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.notification.channel.NotificationChannel;
import com.rathinam.toastmasters.modules.notification.dto.CreateNotificationRequest;
import com.rathinam.toastmasters.modules.notification.dto.NotificationResponse;
import com.rathinam.toastmasters.modules.notification.dto.UnreadNotificationCountResponse;
import com.rathinam.toastmasters.modules.notification.entity.NotificationEntity;
import com.rathinam.toastmasters.modules.notification.entity.NotificationType;
import com.rathinam.toastmasters.modules.notification.exception.NotificationNotFoundException;
import com.rathinam.toastmasters.modules.notification.mapper.NotificationMapper;
import com.rathinam.toastmasters.modules.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final NotificationMapper notificationMapper;
    private final List<NotificationChannel> notificationChannels;

    public NotificationService(NotificationRepository notificationRepository,
                               MemberRepository memberRepository,
                               MeetingRepository meetingRepository,
                               NotificationMapper notificationMapper,
                               List<NotificationChannel> notificationChannels) {
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.notificationMapper = notificationMapper;
        this.notificationChannels = notificationChannels;
    }

    @Transactional
    public NotificationResponse notifyMember(UUID memberId,
                                              NotificationType type,
                                              String title,
                                              String message,
                                              UUID meetingId,
                                              String sourceType,
                                              UUID sourceId) {
        if (sourceType != null && sourceId != null &&
                notificationRepository.existsBySourceTypeAndSourceIdAndMemberId(sourceType, sourceId, memberId)) {
            return null;
        }

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        MeetingEntity meeting = null;
        if (meetingId != null) {
            meeting = meetingRepository.findById(meetingId).orElse(null);
        }

        NotificationEntity entity = new NotificationEntity();
        entity.setMember(member);
        entity.setType(type);
        entity.setTitle(title);
        entity.setMessage(message);
        entity.setMeeting(meeting);
        entity.setSourceType(sourceType);
        entity.setSourceId(sourceId);

        for (NotificationChannel channel : notificationChannels) {
            channel.send(entity);
        }

        return notificationMapper.toResponse(entity);
    }

    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        return notifyMember(
                request.getMemberId(),
                request.getType(),
                request.getTitle(),
                request.getMessage(),
                request.getMeetingId(),
                request.getSourceType(),
                request.getSourceId()
        );
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMemberNotifications(UUID memberId, Pageable pageable) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUnreadNotifications(UUID memberId, Pageable pageable) {
        return notificationRepository.findByMemberIdAndReadAtIsNullOrderByCreatedAtDesc(memberId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse countUnreadNotifications(UUID memberId) {
        long count = notificationRepository.countByMemberIdAndReadAtIsNull(memberId);
        return new UnreadNotificationCountResponse(count);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, UUID memberId) {
        NotificationEntity notification = notificationRepository.findByIdAndMemberId(notificationId, memberId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found or access denied"));

        if (notification.getReadAt() == null) {
            notification.setReadAt(OffsetDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public int markAllAsRead(UUID memberId) {
        return notificationRepository.markAllAsReadForMember(memberId, OffsetDateTime.now());
    }
}
