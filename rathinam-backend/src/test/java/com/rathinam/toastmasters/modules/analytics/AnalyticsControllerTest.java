package com.rathinam.toastmasters.modules.analytics;

import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.analytics.dto.ClubOverviewAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MemberAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MonthlyAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.dto.MonthlyReportResponse;
import com.rathinam.toastmasters.modules.analytics.service.AnalyticsService;
import com.rathinam.toastmasters.modules.ranking.exception.InvalidRankingPeriodException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

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
    void getMemberAnalytics_Returns200OK() throws Exception {
        MemberAnalyticsResponse response = new MemberAnalyticsResponse(memberId, "Pravin", "pravin@test.com", 5, 50.0, 3, 50, 1, 2L);
        when(analyticsService.getMemberAnalytics(memberId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/members/{memberId}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.displayName").value("Pravin"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMeetingAnalytics_Returns200OK() throws Exception {
        MeetingAnalyticsResponse response = new MeetingAnalyticsResponse();
        response.setMeetingId(meetingId);
        response.setMeetingNumber(101);
        when(analyticsService.getMeetingAnalytics(meetingId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/meetings/{meetingId}", meetingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.meetingNumber").value(101));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getClubOverviewAnalytics_Returns200OK() throws Exception {
        ClubOverviewAnalyticsResponse response = new ClubOverviewAnalyticsResponse(20, 18, 15, 150, 10.0, 1200, 5, null);
        when(analyticsService.getClubOverviewAnalytics()).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalMembers").value(20));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMonthlyAnalytics_Returns200OK() throws Exception {
        MonthlyAnalyticsResponse response = new MonthlyAnalyticsResponse(2026, 8, 4, 32, 8.0, 300, 18, List.of(), null, 2);
        when(analyticsService.getMonthlyAnalytics(2026, 8)).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/monthly/2026/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalMeetings").value(4));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMonthlyAnalytics_InvalidMonth_Returns400BadRequest() throws Exception {
        when(analyticsService.getMonthlyAnalytics(2026, 13)).thenThrow(new InvalidRankingPeriodException("Invalid year or month: 2026-13"));

        mockMvc.perform(get("/api/v1/analytics/monthly/2026/13"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMemberPerformanceTrend_Returns200OK() throws Exception {
        when(analyticsService.getMemberPerformanceTrend(memberId, 6)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/analytics/members/{memberId}/performance", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMonthlyReport_Returns200OK() throws Exception {
        MonthlyReportResponse response = new MonthlyReportResponse("AUGUST 2026", 2026, 8, null, null, null, List.of(), null, 0);
        when(analyticsService.generateMonthlyReport(2026, 8)).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/reports/monthly/2026/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reportingPeriod").value("AUGUST 2026"));
    }

    @Test
    void getOverviewAnalytics_Unauthenticated_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/overview"))
                .andExpect(status().isForbidden());
    }
}
