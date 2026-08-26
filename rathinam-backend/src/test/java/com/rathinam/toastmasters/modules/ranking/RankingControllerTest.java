package com.rathinam.toastmasters.modules.ranking;

import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.ranking.dto.LeaderboardResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MeetingRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MemberRankingResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.service.RankingService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RankingService rankingService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID memberId;
    private UUID meetingId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        meetingId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getLeaderboard_AuthorizedMember_Returns200OK() throws Exception {
        LeaderboardResponse response = new LeaderboardResponse(LocalDateTime.now().minusDays(30), LocalDateTime.now(), new PageImpl<>(List.of()));
        when(rankingService.getLeaderboard(any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/rankings/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getLeaderboard_Unauthenticated_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/rankings/leaderboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void getMonthlyRanking_OfficerRole_Returns200OK() throws Exception {
        MonthlyRankingResponse response = new MonthlyRankingResponse(2026, 8, 0L, new PageImpl<>(List.of()), null);
        when(rankingService.getMonthlyRanking(eq(2026), eq(8), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/rankings/monthly/2026/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.year").value(2026))
                .andExpect(jsonPath("$.data.month").value(8));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMemberRanking_MemberRole_Returns200OK() throws Exception {
        MemberRankingResponse response = new MemberRankingResponse(memberId, "Pravin", 148L, 1, 2026, 8);
        when(rankingService.getMemberRanking(eq(memberId), eq(2026), eq(8))).thenReturn(response);

        mockMvc.perform(get("/api/v1/rankings/member/{memberId}?year=2026&month=8", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rank").value(1))
                .andExpect(jsonPath("$.data.totalPoints").value(148));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMeetingRankings_Returns200OK() throws Exception {
        MeetingRankingResponse response = new MeetingRankingResponse(meetingId, 25, List.of());
        when(rankingService.getMeetingRankings(meetingId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/rankings/meetings/{meetingId}", meetingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.meetingNumber").value(25));
    }
}
