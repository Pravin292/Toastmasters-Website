package com.rathinam.toastmasters.modules.certificate.repository;

import com.rathinam.toastmasters.modules.certificate.entity.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateEntity, UUID> {
    Optional<CertificateEntity> findByCertificateNumberIgnoreCase(String certificateNumber);
    boolean existsByCertificateNumberIgnoreCase(String certificateNumber);
    List<CertificateEntity> findByMemberId(UUID memberId);
}
