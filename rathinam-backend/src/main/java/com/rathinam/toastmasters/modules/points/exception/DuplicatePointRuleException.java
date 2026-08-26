package com.rathinam.toastmasters.modules.points.exception;

public class DuplicatePointRuleException extends RuntimeException {
    public DuplicatePointRuleException(String code) {
        super("A point rule with code '" + code + "' already exists");
    }
}
