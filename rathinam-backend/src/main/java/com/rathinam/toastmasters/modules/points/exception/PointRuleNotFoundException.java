package com.rathinam.toastmasters.modules.points.exception;

import java.util.UUID;

public class PointRuleNotFoundException extends RuntimeException {
    public PointRuleNotFoundException(UUID id) {
        super("Point rule not found with ID: " + id);
    }

    public PointRuleNotFoundException(String code) {
        super("Point rule not found with code: " + code);
    }
}
