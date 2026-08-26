package com.rathinam.toastmasters.modules.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.attendance.dto.AttendanceResponse;
import com.rathinam.toastmasters.modules.attendance.dto.CreateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.dto.UpdateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceStatus;
import com.rathinam.toastmasters.modules.attendance.exception.AttendanceNotFoundException;
import com.rathinam.toastmasters.modules.attendance.exception.DuplicateAttendanceException;
import com.rathinam.toastmasters.modules.attendance.service.AttendanceService;
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
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttendanceService attendanceService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID meetingId;
    private UUID memberId;
    private UUID attendanceId;
    private AttendanceResponse attendanceResponse;

    @BeforeEach
    void setUp() {
        meetingId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        attendanceId = UUID.randomUUID();

        attendanceResponse = new AttendanceResponse();
        attendanceResponse.setId(attendanceId);
        attendanceResponse.setMeetingId(meetingId);
        attendanceResponse.setMeetingNumber(101);
        attendanceResponse.setMemberId(memberId);
        attendanceResponse.setMemberDisplayName("John Doe");
        attendanceResponse.setStatus(AttendanceStatus.PRESENT);
        attendanceResponse.setCheckInTime(OffsetDateTime.parse("2026-09-01T18:05:00+05:30"));
    }

    @Test
    @WithMockUser
    void recordAttendance_WithValidRequest_Returns201Created() throws Exception {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.PRESENT);

        when(attendanceService.recordAttendance(eq(meetingId), any(CreateAttendanceRequest.class)))
                .thenReturn(attendanceResponse);

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/attendance", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(attendanceId.toString()))
            .andExpect(jsonPath("$.data.status").value("PRESENT"));
    }

    @Test
    @WithMockUser
    void recordAttendance_WithInvalidRequest_Returns400BadRequest() throws Exception {
        CreateAttendanceRequest invalidRequest = new CreateAttendanceRequest(null, null);

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/attendance", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @WithMockUser
    void recordAttendance_WithDuplicateRecord_Returns409Conflict() throws Exception {
        CreateAttendanceRequest request = new CreateAttendanceRequest(memberId, AttendanceStatus.PRESENT);

        when(attendanceService.recordAttendance(eq(meetingId), any(CreateAttendanceRequest.class)))
                .thenThrow(new DuplicateAttendanceException(meetingId, memberId));

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/attendance", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Attendance record already exists for member " + memberId + " at meeting " + meetingId));
    }

    @Test
    @WithMockUser
    void getMeetingAttendance_Returns200OK() throws Exception {
        when(attendanceService.getMeetingAttendance(meetingId)).thenReturn(List.of(attendanceResponse));

        mockMvc.perform(get("/api/v1/meetings/{meetingId}/attendance", meetingId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(attendanceId.toString()))
            .andExpect(jsonPath("$.data[0].status").value("PRESENT"));
    }

    @Test
    @WithMockUser
    void getAttendanceById_WhenExists_Returns200OK() throws Exception {
        when(attendanceService.getAttendanceById(attendanceId)).thenReturn(attendanceResponse);

        mockMvc.perform(get("/api/v1/attendance/{attendanceId}", attendanceId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(attendanceId.toString()));
    }

    @Test
    @WithMockUser
    void getAttendanceById_WhenMissing_Returns404NotFound() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(attendanceService.getAttendanceById(unknownId)).thenThrow(new AttendanceNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/attendance/{attendanceId}", unknownId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Attendance record not found with ID: " + unknownId));
    }

    @Test
    @WithMockUser
    void updateAttendance_WithValidRequest_Returns200OK() throws Exception {
        UpdateAttendanceRequest updateRequest = new UpdateAttendanceRequest(AttendanceStatus.EXCUSED);
        attendanceResponse.setStatus(AttendanceStatus.EXCUSED);

        when(attendanceService.updateAttendance(eq(attendanceId), any(UpdateAttendanceRequest.class)))
                .thenReturn(attendanceResponse);

        mockMvc.perform(patch("/api/v1/attendance/{attendanceId}", attendanceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("EXCUSED"));
    }

    @Test
    void getMeetingAttendance_WithoutAuthentication_Returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/meetings/{meetingId}/attendance", meetingId))
            .andExpect(status().isForbidden());
    }
}
