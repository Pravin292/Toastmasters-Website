package com.rathinam.toastmasters.modules.achievement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.achievement.dto.AchievementDefinitionResponse;
import com.rathinam.toastmasters.modules.achievement.dto.CreateAchievementDefinitionRequest;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCriteriaType;
import com.rathinam.toastmasters.modules.achievement.service.AchievementDefinitionService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AchievementDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AchievementDefinitionService definitionService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID definitionId;

    @BeforeEach
    void setUp() {
        definitionId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void createAchievementDefinition_Officer_Returns201Created() throws Exception {
        CreateAchievementDefinitionRequest request = new CreateAchievementDefinitionRequest("FIRST_STEP", "First Step", "Attended first meeting", "footsteps", AchievementCategory.ATTENDANCE, AchievementCriteriaType.ATTENDANCE_COUNT, 1);
        AchievementDefinitionResponse response = new AchievementDefinitionResponse();
        response.setId(definitionId);
        response.setCode("FIRST_STEP");
        response.setName("First Step");
        response.setDescription("Attended first meeting");
        response.setIcon("footsteps");
        response.setCategory(AchievementCategory.ATTENDANCE);
        response.setCriteriaType(AchievementCriteriaType.ATTENDANCE_COUNT);
        response.setCriteriaThreshold(1);
        response.setRepeatable(false);
        response.setActive(true);

        when(definitionService.createAchievementDefinition(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/achievements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("FIRST_STEP"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void createAchievementDefinition_MemberRole_Returns403Forbidden() throws Exception {
        CreateAchievementDefinitionRequest request = new CreateAchievementDefinitionRequest("FIRST_STEP", "First Step", "Attended first meeting", "footsteps", AchievementCategory.ATTENDANCE, AchievementCriteriaType.ATTENDANCE_COUNT, 1);

        mockMvc.perform(post("/api/v1/achievements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getAllAchievementDefinitions_Returns200OK() throws Exception {
        AchievementDefinitionResponse response = new AchievementDefinitionResponse();
        response.setId(definitionId);
        response.setCode("FIRST_STEP");
        response.setName("First Step");
        response.setDescription("Attended first meeting");
        response.setIcon("footsteps");
        response.setCategory(AchievementCategory.ATTENDANCE);
        response.setCriteriaType(AchievementCriteriaType.ATTENDANCE_COUNT);
        response.setCriteriaThreshold(1);
        response.setRepeatable(false);
        response.setActive(true);

        when(definitionService.getAllAchievementDefinitions(null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].code").value("FIRST_STEP"));
    }
}
