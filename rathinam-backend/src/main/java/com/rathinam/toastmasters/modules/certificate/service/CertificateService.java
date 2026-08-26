package com.rathinam.toastmasters.modules.certificate.service;

import com.rathinam.toastmasters.modules.achievement.entity.MemberAchievementEntity;
import com.rathinam.toastmasters.modules.achievement.repository.MemberAchievementRepository;
import com.rathinam.toastmasters.modules.certificate.dto.CertificateResponse;
import com.rathinam.toastmasters.modules.certificate.dto.IssueCertificateRequest;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateEntity;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateStatus;
import com.rathinam.toastmasters.modules.certificate.exception.CertificateNotFoundException;
import com.rathinam.toastmasters.modules.certificate.exception.DuplicateCertificateException;
import com.rathinam.toastmasters.modules.certificate.mapper.CertificateMapper;
import com.rathinam.toastmasters.modules.certificate.repository.CertificateRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final MemberRepository memberRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final CertificateMapper certificateMapper;

    public CertificateService(CertificateRepository certificateRepository,
                              MemberRepository memberRepository,
                              MemberAchievementRepository memberAchievementRepository,
                              CertificateMapper certificateMapper) {
        this.certificateRepository = certificateRepository;
        this.memberRepository = memberRepository;
        this.memberAchievementRepository = memberAchievementRepository;
        this.certificateMapper = certificateMapper;
    }

    public CertificateResponse issueCertificate(IssueCertificateRequest request) {
        MemberEntity member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));

        String certNum = (request.getCustomCertificateNumber() != null && !request.getCustomCertificateNumber().isBlank())
                ? request.getCustomCertificateNumber().trim()
                : generateCertificateNumber();

        if (certificateRepository.existsByCertificateNumberIgnoreCase(certNum)) {
            throw new DuplicateCertificateException(certNum);
        }

        MemberAchievementEntity achievement = null;
        if (request.getAchievementId() != null) {
            achievement = memberAchievementRepository.findById(request.getAchievementId()).orElse(null);
        }

        CertificateEntity entity = new CertificateEntity();
        entity.setCertificateNumber(certNum);
        entity.setMember(member);
        entity.setCertificateType(request.getCertificateType());
        entity.setTitle(request.getTitle().trim());
        entity.setDescription(request.getDescription().trim());
        entity.setIssuedDate(OffsetDateTime.now());
        entity.setAchievement(achievement);
        entity.setStatus(CertificateStatus.ISSUED);

        CertificateEntity saved = certificateRepository.save(entity);
        return certificateMapper.toCertificateResponse(saved);
    }

    @Transactional(readOnly = true)
    public CertificateResponse getCertificateById(UUID id) {
        CertificateEntity entity = certificateRepository.findById(id)
                .orElseThrow(() -> new CertificateNotFoundException(id));
        return certificateMapper.toCertificateResponse(entity);
    }

    @Transactional(readOnly = true)
    public CertificateResponse getCertificateByNumber(String certificateNumber) {
        CertificateEntity entity = certificateRepository.findByCertificateNumberIgnoreCase(certificateNumber)
                .orElseThrow(() -> new CertificateNotFoundException(certificateNumber));
        return certificateMapper.toCertificateResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> getMemberCertificates(UUID memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }

        return certificateRepository.findByMemberId(memberId).stream()
                .map(certificateMapper::toCertificateResponse)
                .collect(Collectors.toList());
    }

    private String generateCertificateNumber() {
        return "CERT-" + OffsetDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
