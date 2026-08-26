package com.rathinam.toastmasters.modules.meetingrole;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleDefinitionResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.UpdateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateRoleDefinitionException;
import com.rathinam.toastmasters.modules.meetingrole.service.RoleDefinitionService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleDefinitionService roleDefinitionService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID roleId;
    private RoleDefinitionResponse roleResponse;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        roleResponse = new RoleDefinitionResponse();
        roleResponse.setId(roleId);
        roleResponse.setName("Timer");
        roleResponse.setDescription("Keeps track of time");
        roleResponse.setActive(true);
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void createRoleDefinition_Authorized_Success() throws Exception {
        CreateRoleDefinitionRequest request = new CreateRoleDefinitionRequest("Timer", "Keeps track of time");
        when(roleDefinitionService.createRoleDefinition(any(CreateRoleDefinitionRequest.class))).thenReturn(roleResponse);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(roleId.toString()))
            .andExpect(jsonPath("$.data.name").value("Timer"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void createRoleDefinition_UnauthorizedMember_Returns403Forbidden() throws Exception {
        CreateRoleDefinitionRequest request = new CreateRoleDefinitionRequest("Timer", "Keeps track of time");

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void createRoleDefinition_Duplicate_Returns409Conflict() throws Exception {
        CreateRoleDefinitionRequest request = new CreateRoleDefinitionRequest("Timer", "Keeps track of time");
        when(roleDefinitionService.createRoleDefinition(any(CreateRoleDefinitionRequest.class)))
                .thenThrow(new DuplicateRoleDefinitionException("Timer"));

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("A role definition with name 'Timer' already exists"));
    }

    @Test
    @WithMockUser
    void getAllRoleDefinitions_Returns200OK() throws Exception {
        when(roleDefinitionService.getAllRoleDefinitions(null)).thenReturn(List.of(roleResponse));

        mockMvc.perform(get("/api/v1/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].name").value("Timer"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoleDefinition_Authorized_Success() throws Exception {
        UpdateRoleDefinitionRequest request = new UpdateRoleDefinitionRequest();
        request.setActive(false);
        roleResponse.setActive(false);

        when(roleDefinitionService.updateRoleDefinition(eq(roleId), any(UpdateRoleDefinitionRequest.class)))
                .thenReturn(roleResponse);

        mockMvc.perform(patch("/api/v1/roles/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.active").value(false));
    }
}
