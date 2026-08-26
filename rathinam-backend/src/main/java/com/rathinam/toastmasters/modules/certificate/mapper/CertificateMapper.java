package com.rathinam.toastmasters.modules.certificate.mapper;

import com.rathinam.toastmasters.modules.certificate.dto.CertificateResponse;
import com.rathinam.toastmasters.modules.certificate.entity.CertificateEntity;
import org.springframework.stereotype.Component;

@Component
public class CertificateMapper {

    public CertificateResponse toCertificateResponse(CertificateEntity entity) {
        if (entity == null) return null;
        CertificateResponse response = new CertificateResponse();
        response.setId(entity.getId());
        response.setCertificateNumber(entity.getCertificateNumber());
        if (entity.getMember() != null) {
            response.setMemberId(entity.getMember().getId());
            response.setMemberDisplayName(entity.getMember().getDisplayName());
        }
        response.setCertificateType(entity.getCertificateType());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setIssuedDate(entity.getIssuedDate());
        if (entity.getAchievement() != null) {
            response.setAchievementId(entity.getAchievement().getId());
        }
        response.setStatus(entity.getStatus());
        return response;
    }
}
