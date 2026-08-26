package com.rathinam.toastmasters.modules.member.exception;

import java.util.UUID;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(UUID id) {
        super("Member not found with ID: " + id);
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
