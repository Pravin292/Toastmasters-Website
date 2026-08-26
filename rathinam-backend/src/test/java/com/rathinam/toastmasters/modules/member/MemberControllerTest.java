package com.rathinam.toastmasters.modules.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.modules.member.dto.CreateMemberRequest;
import com.rathinam.toastmasters.modules.member.dto.MemberResponse;
import com.rathinam.toastmasters.modules.member.dto.UpdateMemberRequest;
import com.rathinam.toastmasters.modules.member.entity.MemberStatus;
import com.rathinam.toastmasters.modules.member.exception.DuplicateEmailException;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.service.MemberService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.time.LocalDate;
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
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    private UUID memberId;
    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();

        memberResponse = new MemberResponse();
        memberResponse.setId(memberId);
        memberResponse.setFirstName("John");
        memberResponse.setLastName("Doe");
        memberResponse.setDisplayName("John Doe");
        memberResponse.setEmail("john.doe@example.com");
        memberResponse.setJoinDate(LocalDate.of(2026, 1, 1));
        memberResponse.setStatus(MemberStatus.ACTIVE);
        memberResponse.setBio("Founding Member");
    }

    @Test
    @WithMockUser
    void createMember_WithValidRequest_Returns201Created() throws Exception {
        CreateMemberRequest request = new CreateMemberRequest("John", "Doe", "john.doe@example.com", LocalDate.of(2026, 1, 1));
        when(memberService.createMember(any(CreateMemberRequest.class))).thenReturn(memberResponse);

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(memberId.toString()))
            .andExpect(jsonPath("$.data.firstName").value("John"))
            .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser
    void createMember_WithInvalidRequest_Returns400BadRequest() throws Exception {
        CreateMemberRequest invalidRequest = new CreateMemberRequest("", "", "invalid-email", null);

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.data.firstName").exists())
            .andExpect(jsonPath("$.data.email").exists());
    }

    @Test
    @WithMockUser
    void createMember_WithDuplicateEmail_Returns409Conflict() throws Exception {
        CreateMemberRequest request = new CreateMemberRequest("John", "Doe", "duplicate@example.com", LocalDate.of(2026, 1, 1));
        when(memberService.createMember(any(CreateMemberRequest.class)))
            .thenThrow(new DuplicateEmailException("duplicate@example.com"));

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("A member with email 'duplicate@example.com' already exists"));
    }

    @Test
    @WithMockUser
    void getMemberById_WhenMemberExists_Returns200OK() throws Exception {
        when(memberService.getMemberById(memberId)).thenReturn(memberResponse);

        mockMvc.perform(get("/api/v1/members/{id}", memberId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(memberId.toString()))
            .andExpect(jsonPath("$.data.displayName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void getMemberById_WhenMemberDoesNotExist_Returns404NotFound() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(memberService.getMemberById(unknownId)).thenThrow(new MemberNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/members/{id}", unknownId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Member not found with ID: " + unknownId));
    }

    @Test
    @WithMockUser
    void updateMember_WithValidRequest_Returns200OK() throws Exception {
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setFirstName("Johnny");

        memberResponse.setFirstName("Johnny");
        when(memberService.updateMember(eq(memberId), any(UpdateMemberRequest.class))).thenReturn(memberResponse);

        mockMvc.perform(patch("/api/v1/members/{id}", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.firstName").value("Johnny"));
    }
}
