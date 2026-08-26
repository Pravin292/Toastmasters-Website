package com.rathinam.toastmasters.modules.meeting;

import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingResponse;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingWorkflowResponse;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.exception.InvalidMeetingStatusTransitionException;
import com.rathinam.toastmasters.modules.meeting.service.MeetingService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MeetingWorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeetingService meetingService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID meetingId;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void startMeeting_OfficerRole_Returns200OK() throws Exception {
        MeetingResponse response = new MeetingResponse();
        response.setId(meetingId);
        response.setStatus(MeetingStatus.IN_PROGRESS);

        when(meetingService.startMeeting(meetingId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/meetings/{id}/start", meetingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void startMeeting_MemberRole_Returns403Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/meetings/{id}/start", meetingId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void completeMeeting_OfficerRole_Returns200OK() throws Exception {
        MeetingResponse response = new MeetingResponse();
        response.setId(meetingId);
        response.setStatus(MeetingStatus.COMPLETED);

        when(meetingService.completeMeeting(meetingId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/meetings/{id}/complete", meetingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void completeMeeting_MemberRole_Returns403Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/meetings/{id}/complete", meetingId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMeetingWorkflow_Returns200OK() throws Exception {
        MeetingWorkflowResponse response = new MeetingWorkflowResponse();
        response.setMeetingId(meetingId);
        response.setStatus(MeetingStatus.SCHEDULED);
        response.setCanStart(true);
        response.setCanComplete(false);
        response.setWorkflowWarnings(List.of("No attendance recorded"));

        when(meetingService.getMeetingWorkflow(meetingId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/meetings/{id}/workflow", meetingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canStart").value(true));
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void startMeeting_InvalidTransition_Returns400BadRequest() throws Exception {
        when(meetingService.startMeeting(meetingId))
                .thenThrow(new InvalidMeetingStatusTransitionException("Cannot transition meeting from COMPLETED to IN_PROGRESS"));

        mockMvc.perform(post("/api/v1/meetings/{id}/start", meetingId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getWorkflow_Unauthenticated_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/meetings/{id}/workflow", meetingId))
                .andExpect(status().isForbidden());
    }
}
