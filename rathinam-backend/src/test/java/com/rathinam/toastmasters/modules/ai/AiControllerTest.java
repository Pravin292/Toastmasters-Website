package com.rathinam.toastmasters.modules.ai;

import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.ai.dto.MeetingSummaryResponse;
import com.rathinam.toastmasters.modules.ai.exception.AiServiceUnavailableException;
import com.rathinam.toastmasters.modules.ai.service.AiMeetingSummaryService;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;

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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiMeetingSummaryService summaryService;

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
    @WithMockUser(roles = "MEMBER")
    void generateMeetingSummary_Returns200OK() throws Exception {
        MeetingSummaryResponse response = new MeetingSummaryResponse();
        response.setMeetingId(meetingId);
        response.setMeetingNumber(101);
        response.setConciseSummary("AI generated summary text.");
        response.setAiProvider("gemini");
        response.setAiModelUsed("gemini-1.5-flash");
        response.setAiGenerated(true);

        when(summaryService.generateMeetingSummary(eq(meetingId), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ai/meetings/{meetingId}/summary", meetingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"focusArea\": \"Leadership\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conciseSummary").value("AI generated summary text."));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void generateMeetingSummary_MeetingNotFound_Returns404NotFound() throws Exception {
        when(summaryService.generateMeetingSummary(eq(meetingId), any()))
                .thenThrow(new MeetingNotFoundException(meetingId));

        mockMvc.perform(post("/api/v1/ai/meetings/{meetingId}/summary", meetingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void generateMeetingSummary_AiUnavailable_Returns503ServiceUnavailable() throws Exception {
        when(summaryService.generateMeetingSummary(eq(meetingId), any()))
                .thenThrow(new AiServiceUnavailableException("Gemini service unavailable."));

        mockMvc.perform(post("/api/v1/ai/meetings/{meetingId}/summary", meetingId))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void generateMeetingSummary_Unauthenticated_Returns403Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/ai/meetings/{meetingId}/summary", meetingId))
                .andExpect(status().isForbidden());
    }
}
