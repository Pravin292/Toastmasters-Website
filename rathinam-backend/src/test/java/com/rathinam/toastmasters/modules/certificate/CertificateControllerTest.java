package com.rathinam.toastmasters.modules.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.certificate.dto.CertificateResponse;
import com.rathinam.toastmasters.modules.certificate.dto.IssueCertificateRequest;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateStatus;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateType;
import com.rathinam.toastmasters.modules.certificate.service.CertificateService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CertificateService certificateService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID memberId;
    private UUID certificateId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        certificateId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void issueCertificate_Officer_Returns201Created() throws Exception {
        IssueCertificateRequest request = new IssueCertificateRequest(memberId, CertificateType.MONTHLY_CHAMPION, "August 2026 Monthly Champion", "Awarded for highest points");
        CertificateResponse response = new CertificateResponse();
        response.setId(certificateId);
        response.setCertificateNumber("CERT-2026-001");
        response.setMemberId(memberId);
        response.setMemberDisplayName("Pravin");
        response.setCertificateType(CertificateType.MONTHLY_CHAMPION);
        response.setTitle("August 2026 Monthly Champion");
        response.setDescription("Awarded for highest points");
        response.setIssuedDate(OffsetDateTime.now());
        response.setStatus(CertificateStatus.ISSUED);

        when(certificateService.issueCertificate(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/certificates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.certificateNumber").value("CERT-2026-001"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMemberCertificates_Returns200OK() throws Exception {
        CertificateResponse response = new CertificateResponse();
        response.setId(certificateId);
        response.setCertificateNumber("CERT-2026-001");
        response.setMemberId(memberId);
        response.setMemberDisplayName("Pravin");
        response.setCertificateType(CertificateType.MONTHLY_CHAMPION);
        response.setTitle("August 2026 Monthly Champion");
        response.setDescription("Awarded for highest points");
        response.setIssuedDate(OffsetDateTime.now());
        response.setStatus(CertificateStatus.ISSUED);

        when(certificateService.getMemberCertificates(memberId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/members/{memberId}/certificates", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("August 2026 Monthly Champion"));
    }
}
