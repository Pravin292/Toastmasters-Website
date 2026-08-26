package com.rathinam.toastmasters.modules.points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.points.dto.LeaderboardEntryResponse;
import com.rathinam.toastmasters.modules.points.dto.ManualPointAdjustmentRequest;
import com.rathinam.toastmasters.modules.points.dto.MemberPointsSummaryResponse;
import com.rathinam.toastmasters.modules.points.dto.PointEventResponse;
import com.rathinam.toastmasters.modules.points.exception.PointEventNotFoundException;
import com.rathinam.toastmasters.modules.points.service.PointAwardService;
import com.rathinam.toastmasters.modules.points.service.PointEventService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PointEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointAwardService pointAwardService;

    @MockitoBean
    private PointEventService pointEventService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID memberId;
    private UUID meetingId;
    private UUID eventId;
    private PointEventResponse eventResponse;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        meetingId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        eventResponse = new PointEventResponse();
        eventResponse.setId(eventId);
        eventResponse.setMemberId(memberId);
        eventResponse.setMemberDisplayName("Pravin");
        eventResponse.setMeetingId(meetingId);
        eventResponse.setMeetingNumber(25);
        eventResponse.setPoints(10);
        eventResponse.setReason("Toastmaster of the Day");
        eventResponse.setSourceType("MEETING_ROLE");
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void awardManualPoints_Authorized_Success() throws Exception {
        ManualPointAdjustmentRequest request = new ManualPointAdjustmentRequest(memberId, 5, "Special Event Contribution");
        eventResponse.setPoints(5);
        eventResponse.setReason("Special Event Contribution");
        eventResponse.setSourceType("MANUAL");

        when(pointAwardService.awardManualPoints(any(ManualPointAdjustmentRequest.class))).thenReturn(eventResponse);

        mockMvc.perform(post("/api/v1/points/manual")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.points").value(5))
            .andExpect(jsonPath("$.data.reason").value("Special Event Contribution"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void awardManualPoints_UnauthorizedMember_Returns403Forbidden() throws Exception {
        ManualPointAdjustmentRequest request = new ManualPointAdjustmentRequest(memberId, 5, "Special Event Contribution");

        mockMvc.perform(post("/api/v1/points/manual")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getPointEventById_Success() throws Exception {
        when(pointEventService.getPointEventById(eventId)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/v1/points/{eventId}", eventId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(eventId.toString()))
            .andExpect(jsonPath("$.data.points").value(10));
    }

    @Test
    @WithMockUser
    void getPointEventById_NotFound_Returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(pointEventService.getPointEventById(unknownId)).thenThrow(new PointEventNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/points/{eventId}", unknownId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser
    void getMemberPointsSummary_Success() throws Exception {
        MemberPointsSummaryResponse summary = new MemberPointsSummaryResponse();
        summary.setMemberId(memberId);
        summary.setMemberDisplayName("Pravin");
        summary.setTotalPoints(15);
        summary.setEvents(new PageImpl<>(List.of(eventResponse)));

        when(pointEventService.getMemberPointsSummary(eq(memberId), any(), any(), any())).thenReturn(summary);

        mockMvc.perform(get("/api/v1/members/{memberId}/points", memberId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalPoints").value(15));
    }

    @Test
    @WithMockUser
    void getLeaderboard_Success() throws Exception {
        LeaderboardEntryResponse entry = new LeaderboardEntryResponse(memberId, "Pravin", "pravin@test.com", 25L);
        entry.setRank(1);

        when(pointEventService.getLeaderboard(any(), any(), anyInt())).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/v1/points/leaderboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].rank").value(1))
            .andExpect(jsonPath("$.data[0].totalPoints").value(25));
    }

    @Test
    void getLeaderboard_Unauthenticated_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/points/leaderboard"))
            .andExpect(status().isForbidden());
    }
}
