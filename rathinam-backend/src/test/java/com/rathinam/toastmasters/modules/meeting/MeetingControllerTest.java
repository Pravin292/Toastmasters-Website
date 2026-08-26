package com.rathinam.toastmasters.modules.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.meeting.dto.CreateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingResponse;
import com.rathinam.toastmasters.modules.meeting.dto.UpdateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;
import com.rathinam.toastmasters.modules.meeting.exception.DuplicateMeetingNumberException;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.service.MeetingService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
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
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MeetingService meetingService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID meetingId;
    private MeetingResponse meetingResponse;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();

        meetingResponse = new MeetingResponse();
        meetingResponse.setId(meetingId);
        meetingResponse.setMeetingNumber(101);
        meetingResponse.setMeetingStart(OffsetDateTime.parse("2026-09-01T18:00:00+05:30"));
        meetingResponse.setTheme("Embracing Change");
        meetingResponse.setMeetingType(MeetingType.REGULAR);
        meetingResponse.setStatus(MeetingStatus.SCHEDULED);
        meetingResponse.setLocation("Auditorium Hall A");
    }

    @Test
    @WithMockUser
    void createMeeting_WithValidRequest_Returns201Created() throws Exception {
        CreateMeetingRequest request = new CreateMeetingRequest(101, OffsetDateTime.parse("2026-09-01T18:00:00+05:30"), MeetingType.REGULAR);
        request.setTheme("Embracing Change");

        when(meetingService.createMeeting(any(CreateMeetingRequest.class))).thenReturn(meetingResponse);

        mockMvc.perform(post("/api/v1/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(meetingId.toString()))
            .andExpect(jsonPath("$.data.meetingNumber").value(101))
            .andExpect(jsonPath("$.data.theme").value("Embracing Change"));
    }

    @Test
    @WithMockUser
    void createMeeting_WithInvalidRequest_Returns400BadRequest() throws Exception {
        CreateMeetingRequest invalidRequest = new CreateMeetingRequest(null, null, null);

        mockMvc.perform(post("/api/v1/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @WithMockUser
    void createMeeting_WithDuplicateNumber_Returns409Conflict() throws Exception {
        CreateMeetingRequest request = new CreateMeetingRequest(101, OffsetDateTime.parse("2026-09-01T18:00:00+05:30"), MeetingType.REGULAR);

        when(meetingService.createMeeting(any(CreateMeetingRequest.class)))
            .thenThrow(new DuplicateMeetingNumberException(101));

        mockMvc.perform(post("/api/v1/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("A meeting with number #101 already exists"));
    }

    @Test
    @WithMockUser
    void getMeetingById_WhenExists_Returns200OK() throws Exception {
        when(meetingService.getMeetingById(meetingId)).thenReturn(meetingResponse);

        mockMvc.perform(get("/api/v1/meetings/{id}", meetingId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(meetingId.toString()))
            .andExpect(jsonPath("$.data.meetingNumber").value(101));
    }

    @Test
    @WithMockUser
    void getMeetingById_WhenDoesNotExist_Returns404NotFound() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(meetingService.getMeetingById(unknownId)).thenThrow(new MeetingNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/meetings/{id}", unknownId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Meeting not found with ID: " + unknownId));
    }

    @Test
    @WithMockUser
    void getMeetings_Paginated_Returns200OK() throws Exception {
        when(meetingService.getMeetings(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(meetingResponse)));

        mockMvc.perform(get("/api/v1/meetings?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].meetingNumber").value(101));
    }

    @Test
    @WithMockUser
    void updateMeeting_WithValidRequest_Returns200OK() throws Exception {
        UpdateMeetingRequest updateRequest = new UpdateMeetingRequest();
        updateRequest.setTheme("Updated Theme");

        meetingResponse.setTheme("Updated Theme");
        when(meetingService.updateMeeting(eq(meetingId), any(UpdateMeetingRequest.class))).thenReturn(meetingResponse);

        mockMvc.perform(patch("/api/v1/meetings/{id}", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.theme").value("Updated Theme"));
    }

    @Test
    void getMeetingById_WithoutAuthentication_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/meetings/{id}", meetingId))
            .andExpect(status().isForbidden());
    }
}
