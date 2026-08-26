package com.rathinam.toastmasters.modules.achievement;

import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.achievement.dto.BadgeResponse;
import com.rathinam.toastmasters.modules.achievement.dto.MemberAchievementResponse;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.achievement.service.AchievementService;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberAchievementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AchievementService achievementService;

    @MockitoBean
    private AchievementEvaluationService evaluationService;

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
    void getMemberAchievements_Returns200OK() throws Exception {
        MemberAchievementResponse response = new MemberAchievementResponse();
        response.setId(UUID.randomUUID());
        response.setMemberId(memberId);
        response.setMemberDisplayName("Pravin");
        response.setAchievementDefinitionId(UUID.randomUUID());
        response.setAchievementCode("FIRST_STEP");
        response.setAchievementName("First Step");
        response.setIcon("footsteps");
        response.setCategory(AchievementCategory.ATTENDANCE);
        response.setEarnedAt(OffsetDateTime.now());
        response.setReason("Achieved");

        when(achievementService.getMemberAchievements(memberId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/members/{memberId}/achievements", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].achievementCode").value("FIRST_STEP"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMemberBadges_Returns200OK() throws Exception {
        BadgeResponse badge = new BadgeResponse();
        badge.setAchievementId(UUID.randomUUID());
        badge.setMemberId(memberId);
        badge.setBadgeName("First Step");
        badge.setDescription("Attended first meeting");
        badge.setIcon("footsteps");
        badge.setCategory(AchievementCategory.ATTENDANCE);
        badge.setEarnedAt(OffsetDateTime.now());

        when(achievementService.getMemberBadges(memberId)).thenReturn(List.of(badge));

        mockMvc.perform(get("/api/v1/members/{memberId}/badges", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].badgeName").value("First Step"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void evaluateMemberAchievements_Returns200OK() throws Exception {
        doNothing().when(evaluationService).evaluateMemberAchievements(memberId);

        mockMvc.perform(post("/api/v1/members/{memberId}/achievements/evaluate", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
