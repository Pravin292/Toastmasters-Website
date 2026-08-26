package com.rathinam.toastmasters.modules.certificate;

import com.rathinam.toastmasters.modules.certificate.dto.CertificateResponse;
import com.rathinam.toastmasters.modules.certificate.dto.IssueCertificateRequest;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateEntity;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateStatus;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateType;
import com.rathinam.toastmasters.modules.certificate.exception.DuplicateCertificateException;
import com.rathinam.toastmasters.modules.certificate.mapper.CertificateMapper;
import com.rathinam.toastmasters.modules.certificate.repository.CertificateRepository;
import com.rathinam.toastmasters.modules.certificate.service.CertificateService;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private CertificateMapper certificateMapper;

    @InjectMocks
    private CertificateService certificateService;

    private UUID memberId;
    private UUID certificateId;
    private MemberEntity member;
    private CertificateEntity certificateEntity;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        certificateId = UUID.randomUUID();

        member = new MemberEntity();
        member.setId(memberId);
        member.setDisplayName("Pravin");

        certificateEntity = new CertificateEntity();
        certificateEntity.setId(certificateId);
        certificateEntity.setCertificateNumber("CERT-2026-001");
        certificateEntity.setMember(member);
        certificateEntity.setCertificateType(CertificateType.MONTHLY_CHAMPION);
        certificateEntity.setTitle("August 2026 Monthly Champion");
        certificateEntity.setDescription("Awarded for highest points in August 2026");
        certificateEntity.setIssuedDate(OffsetDateTime.now());
        certificateEntity.setStatus(CertificateStatus.ISSUED);
    }

    @Test
    void issueCertificate_Success() {
        IssueCertificateRequest request = new IssueCertificateRequest(memberId, CertificateType.MONTHLY_CHAMPION, "August 2026 Monthly Champion", "Awarded for highest points");
        request.setCustomCertificateNumber("CERT-2026-001");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(certificateRepository.existsByCertificateNumberIgnoreCase("CERT-2026-001")).thenReturn(false);
        when(certificateRepository.save(any(CertificateEntity.class))).thenReturn(certificateEntity);

        CertificateResponse response = certificateService.issueCertificate(request);

        assertThat(response).isNotNull();
        assertThat(response.getCertificateNumber()).isEqualTo("CERT-2026-001");
        assertThat(response.getMemberDisplayName()).isEqualTo("Pravin");
    }

    @Test
    void issueCertificate_DuplicateNumber_ThrowsException() {
        IssueCertificateRequest request = new IssueCertificateRequest(memberId, CertificateType.MONTHLY_CHAMPION, "August 2026 Monthly Champion", "Awarded for highest points");
        request.setCustomCertificateNumber("CERT-2026-001");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(certificateRepository.existsByCertificateNumberIgnoreCase("CERT-2026-001")).thenReturn(true);

        assertThatThrownBy(() -> certificateService.issueCertificate(request))
                .isInstanceOf(DuplicateCertificateException.class);
    }

    @Test
    void getMemberCertificates_Success() {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(certificateRepository.findByMemberId(memberId)).thenReturn(List.of(certificateEntity));

        List<CertificateResponse> certificates = certificateService.getMemberCertificates(memberId);

        assertThat(certificates).hasSize(1);
        assertThat(certificates.get(0).getTitle()).isEqualTo("August 2026 Monthly Champion");
    }
}
