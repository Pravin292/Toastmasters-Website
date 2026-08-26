package com.rathinam.toastmasters.modules.certificate.exception;

import java.util.UUID;

public class CertificateNotFoundException extends RuntimeException {
    public CertificateNotFoundException(UUID id) {
        super("Certificate not found with ID: " + id);
    }

    public CertificateNotFoundException(String certificateNumber) {
        super("Certificate not found with number: " + certificateNumber);
    }
}
