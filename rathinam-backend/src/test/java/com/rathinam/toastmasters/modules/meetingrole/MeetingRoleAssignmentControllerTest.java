package com.rathinam.toastmasters.modules.meetingrole;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleAssignmentRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleAssignmentResponse;
import com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateMeetingRoleAssignmentException;
import com.rathinam.toastmasters.modules.meetingrole.exception.InactiveRoleDefinitionException;
import com.rathinam.toastmasters.modules.meetingrole.exception.MeetingRoleAssignmentNotFoundException;
import com.rathinam.toastmasters.modules.meetingrole.service.MeetingRoleAssignmentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MeetingRoleAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MeetingRoleAssignmentService assignmentService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID meetingId;
    private UUID roleDefId;
    private UUID memberId;
    private UUID assignmentId;
    private RoleAssignmentResponse assignmentResponse;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        roleDefId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        assignmentId = UUID.randomUUID();

        assignmentResponse = new RoleAssignmentResponse();
        assignmentResponse.setId(assignmentId);
        assignmentResponse.setMeetingId(meetingId);
        assignmentResponse.setMeetingNumber(101);
        assignmentResponse.setRoleDefinitionId(roleDefId);
        assignmentResponse.setRoleName("Timer");
        assignmentResponse.setMemberId(memberId);
        assignmentResponse.setMemberDisplayName("John Doe");
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void assignRole_Authorized_Success() throws Exception {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);

        when(assignmentService.assignRole(eq(meetingId), any(CreateRoleAssignmentRequest.class)))
                .thenReturn(assignmentResponse);

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/roles", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.data.roleName").value("Timer"));
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void assignRole_InactiveRole_Returns400BadRequest() throws Exception {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);

        when(assignmentService.assignRole(eq(meetingId), any(CreateRoleAssignmentRequest.class)))
                .thenThrow(new InactiveRoleDefinitionException(roleDefId));

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/roles", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Role definition with ID '" + roleDefId + "' is inactive and cannot be assigned to new meetings"));
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void assignRole_DuplicateAssignment_Returns409Conflict() throws Exception {
        CreateRoleAssignmentRequest request = new CreateRoleAssignmentRequest(roleDefId, memberId);

        when(assignmentService.assignRole(eq(meetingId), any(CreateRoleAssignmentRequest.class)))
                .thenThrow(new DuplicateMeetingRoleAssignmentException("Member already has a role assigned for this meeting"));

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/roles", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Member already has a role assigned for this meeting"));
    }

    @Test
    @WithMockUser
    void getMeetingRoleAssignments_Returns200OK() throws Exception {
        when(assignmentService.getMeetingRoleAssignments(meetingId)).thenReturn(List.of(assignmentResponse));

        mockMvc.perform(get("/api/v1/meetings/{meetingId}/roles", meetingId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].roleName").value("Timer"));
    }

    @Test
    @WithMockUser
    void getAssignmentById_WhenMissing_Returns404NotFound() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(assignmentService.getAssignmentById(unknownId))
                .thenThrow(new MeetingRoleAssignmentNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/meeting-roles/{assignmentId}", unknownId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Meeting role assignment not found with ID: " + unknownId));
    }

    @Test
    void getMeetingRoleAssignments_WithoutAuthentication_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/meetings/{meetingId}/roles", meetingId))
            .andExpect(status().isForbidden());
    }
}
