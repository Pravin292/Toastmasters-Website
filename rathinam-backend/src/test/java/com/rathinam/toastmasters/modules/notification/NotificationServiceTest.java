package com.rathinam.toastmasters.modules.notification;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.notification.channel.NotificationChannel;
import com.rathinam.toastmasters.modules.notification.dto.NotificationResponse;
import com.rathinam.toastmasters.modules.notification.dto.UnreadNotificationCountResponse;
import com.rathinam.toastmasters.modules.notification.entity.NotificationEntity;
import com.rathinam.toastmasters.modules.notification.entity.NotificationType;
import com.rathinam.toastmasters.modules.notification.exception.NotificationNotFoundException;
import com.rathinam.toastmasters.modules.notification.mapper.NotificationMapper;
import com.rathinam.toastmasters.modules.notification.repository.NotificationRepository;
import com.rathinam.toastmasters.modules.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Spy
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationChannel notificationChannel;

    private NotificationService notificationService;

    private UUID memberId;
    private UUID meetingId;
    private MemberEntity memberEntity;
    private MeetingEntity meetingEntity;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                notificationRepository,
                memberRepository,
                meetingRepository,
                notificationMapper,
                List.of(notificationChannel)
        );

        memberId = UUID.randomUUID();
        meetingId = UUID.randomUUID();

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setEmail("member@toastmasters.com");

        meetingEntity = new MeetingEntity();
        meetingEntity.setId(meetingId);
        meetingEntity.setMeetingNumber(101);
    }

    @Test
    void notifyMember_Success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meetingEntity));

        NotificationResponse response = notificationService.notifyMember(
                memberId,
                NotificationType.ROLE_ASSIGNMENT,
                "Role Assignment",
                "Assigned Toastmaster of the Day",
                meetingId,
                "MEETING_ROLE",
                UUID.randomUUID()
        );

        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(memberId);
        assertThat(response.getType()).isEqualTo(NotificationType.ROLE_ASSIGNMENT);

        verify(notificationChannel).send(any(NotificationEntity.class));
    }

    @Test
    void notifyMember_DuplicateSource_ReturnsNullAndSkipsChannel() {
        UUID sourceId = UUID.randomUUID();
        when(notificationRepository.existsBySourceTypeAndSourceIdAndMemberId("MEETING_ROLE", sourceId, memberId))
                .thenReturn(true);

        NotificationResponse response = notificationService.notifyMember(
                memberId,
                NotificationType.ROLE_ASSIGNMENT,
                "Role Assignment",
                "Assigned Toastmaster of the Day",
                meetingId,
                "MEETING_ROLE",
                sourceId
        );

        assertThat(response).isNull();
        verify(notificationChannel, never()).send(any(NotificationEntity.class));
    }

    @Test
    void notifyMember_MemberNotFound_ThrowsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.notifyMember(
                memberId,
                NotificationType.SYSTEM,
                "System Alert",
                "Test message",
                null,
                null,
                null
        )).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getMemberNotifications_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setMember(memberEntity);
        entity.setType(NotificationType.SYSTEM);
        entity.setTitle("Welcome");

        when(notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable))
                .thenReturn(new PageImpl<>(List.of(entity)));

        Page<NotificationResponse> result = notificationService.getMemberNotifications(memberId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void countUnreadNotifications_Success() {
        when(notificationRepository.countByMemberIdAndReadAtIsNull(memberId)).thenReturn(5L);

        UnreadNotificationCountResponse response = notificationService.countUnreadNotifications(memberId);

        assertThat(response).isNotNull();
        assertThat(response.getUnreadCount()).isEqualTo(5L);
    }

    @Test
    void markAsRead_Success() {
        UUID notificationId = UUID.randomUUID();
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notificationId);
        entity.setMember(memberEntity);
        entity.setReadAt(null);

        when(notificationRepository.findByIdAndMemberId(notificationId, memberId)).thenReturn(Optional.of(entity));
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponse response = notificationService.markAsRead(notificationId, memberId);

        assertThat(response).isNotNull();
        assertThat(response.isRead()).isTrue();
        assertThat(response.getReadAt()).isNotNull();
    }

    @Test
    void markAsRead_NotFoundOrUnowned_ThrowsNotificationNotFoundException() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findByIdAndMemberId(notificationId, memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(notificationId, memberId))
                .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    void markAllAsRead_Success() {
        when(notificationRepository.markAllAsReadForMember(eq(memberId), any(OffsetDateTime.class))).thenReturn(3);

        int count = notificationService.markAllAsRead(memberId);

        assertThat(count).isEqualTo(3);
    }
}
