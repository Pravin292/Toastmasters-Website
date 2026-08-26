package com.rathinam.toastmasters.modules.notification;

import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.notification.dto.NotificationResponse;
import com.rathinam.toastmasters.modules.notification.dto.UnreadNotificationCountResponse;
import com.rathinam.toastmasters.modules.notification.entity.NotificationType;
import com.rathinam.toastmasters.modules.notification.service.NotificationService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID memberId;
    private MemberEntity memberEntity;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setEmail("user@toastmasters.com");

        when(memberRepository.findByEmailIgnoreCase("user@toastmasters.com"))
                .thenReturn(Optional.of(memberEntity));
    }

    @Test
    @WithMockUser(username = "user@toastmasters.com", roles = "MEMBER")
    void getMemberNotifications_Returns200OK() throws Exception {
        NotificationResponse response = new NotificationResponse();
        response.setId(UUID.randomUUID());
        response.setType(NotificationType.ROLE_ASSIGNMENT);
        response.setTitle("Role Assigned");

        when(notificationService.getMemberNotifications(eq(memberId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Role Assigned"));
    }

    @Test
    @WithMockUser(username = "user@toastmasters.com", roles = "MEMBER")
    void getUnreadNotifications_Returns200OK() throws Exception {
        NotificationResponse response = new NotificationResponse();
        response.setId(UUID.randomUUID());
        response.setRead(false);

        when(notificationService.getUnreadNotifications(eq(memberId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "user@toastmasters.com", roles = "MEMBER")
    void countUnreadNotifications_Returns200OK() throws Exception {
        when(notificationService.countUnreadNotifications(memberId))
                .thenReturn(new UnreadNotificationCountResponse(3));

        mockMvc.perform(get("/api/v1/notifications/unread/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.unreadCount").value(3));
    }

    @Test
    @WithMockUser(username = "user@toastmasters.com", roles = "MEMBER")
    void markAsRead_Returns200OK() throws Exception {
        UUID notificationId = UUID.randomUUID();
        NotificationResponse response = new NotificationResponse();
        response.setId(notificationId);
        response.setRead(true);

        when(notificationService.markAsRead(notificationId, memberId)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/notifications/{id}/read", notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.read").value(true));
    }

    @Test
    @WithMockUser(username = "user@toastmasters.com", roles = "MEMBER")
    void markAllAsRead_Returns200OK() throws Exception {
        when(notificationService.markAllAsRead(memberId)).thenReturn(4);

        mockMvc.perform(patch("/api/v1/notifications/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getNotifications_Unauthenticated_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isForbidden());
    }
}
