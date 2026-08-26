package com.rathinam.toastmasters.modules.ranking;

import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyChampionResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.service.ChampionshipService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChampionshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChampionshipService championshipService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID memberId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMonthlyChampionship_Returns200OK() throws Exception {
        MonthlyChampionResponse champion = new MonthlyChampionResponse(2026, 8, memberId, "Pravin", "pravin@test.com", 148L);
        MonthlyRankingResponse response = new MonthlyRankingResponse(2026, 8, 1L, new PageImpl<>(List.of()), champion);

        when(championshipService.getMonthlyChampionship(2026, 8)).thenReturn(response);

        mockMvc.perform(get("/api/v1/championships/monthly/2026/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.champion.displayName").value("Pravin"))
                .andExpect(jsonPath("$.data.champion.points").value(148));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getCurrentMonthlyChampionship_Returns200OK() throws Exception {
        MonthlyRankingResponse response = new MonthlyRankingResponse(2026, 8, 0L, new PageImpl<>(List.of()), null);
        when(championshipService.getCurrentMonthlyChampionship()).thenReturn(response);

        mockMvc.perform(get("/api/v1/championships/monthly/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getCurrentMonthlyChampionship_Unauthenticated_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/championships/monthly/current"))
                .andExpect(status().isForbidden());
    }
}
