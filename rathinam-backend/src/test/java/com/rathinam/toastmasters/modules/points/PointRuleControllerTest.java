package com.rathinam.toastmasters.modules.points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.points.dto.CreatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.dto.PointRuleResponse;
import com.rathinam.toastmasters.modules.points.entity.PointRuleCategory;
import com.rathinam.toastmasters.modules.points.exception.DuplicatePointRuleException;
import com.rathinam.toastmasters.modules.points.service.PointRuleService;
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
class PointRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointRuleService pointRuleService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID ruleId;
    private PointRuleResponse ruleResponse;

    @BeforeEach
    void setUp() {
        ruleId = UUID.randomUUID();
        ruleResponse = new PointRuleResponse();
        ruleResponse.setId(ruleId);
        ruleResponse.setCode("ATTENDANCE_PRESENT");
        ruleResponse.setName("Attendance Present");
        ruleResponse.setPoints(5);
        ruleResponse.setActive(true);
        ruleResponse.setCategory(PointRuleCategory.ATTENDANCE);
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void createPointRule_Authorized_Success() throws Exception {
        CreatePointRuleRequest request = new CreatePointRuleRequest("ATTENDANCE_PRESENT", "Attendance Present", 5, PointRuleCategory.ATTENDANCE);
        when(pointRuleService.createPointRule(any(CreatePointRuleRequest.class))).thenReturn(ruleResponse);

        mockMvc.perform(post("/api/v1/point-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.code").value("ATTENDANCE_PRESENT"))
            .andExpect(jsonPath("$.data.points").value(5));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void createPointRule_UnauthorizedMember_Returns403Forbidden() throws Exception {
        CreatePointRuleRequest request = new CreatePointRuleRequest("ATTENDANCE_PRESENT", "Attendance Present", 5, PointRuleCategory.ATTENDANCE);

        mockMvc.perform(post("/api/v1/point-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void createPointRule_Duplicate_Returns409Conflict() throws Exception {
        CreatePointRuleRequest request = new CreatePointRuleRequest("ATTENDANCE_PRESENT", "Attendance Present", 5, PointRuleCategory.ATTENDANCE);
        when(pointRuleService.createPointRule(any(CreatePointRuleRequest.class)))
                .thenThrow(new DuplicatePointRuleException("ATTENDANCE_PRESENT"));

        mockMvc.perform(post("/api/v1/point-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("A point rule with code 'ATTENDANCE_PRESENT' already exists"));
    }

    @Test
    @WithMockUser
    void getAllPointRules_Returns200OK() throws Exception {
        when(pointRuleService.getAllPointRules(null)).thenReturn(List.of(ruleResponse));

        mockMvc.perform(get("/api/v1/point-rules"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].code").value("ATTENDANCE_PRESENT"));
    }
}
