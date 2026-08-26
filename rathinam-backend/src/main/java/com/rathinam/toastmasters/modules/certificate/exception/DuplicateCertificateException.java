package com.rathinam.toastmasters.modules.certificate.exception;

public class DuplicateCertificateException extends RuntimeException {
    public DuplicateCertificateException(String certificateNumber) {
        super("A certificate with number '" + certificateNumber + "' already exists");
    }
}
