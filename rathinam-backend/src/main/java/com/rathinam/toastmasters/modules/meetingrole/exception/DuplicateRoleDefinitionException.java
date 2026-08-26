package com.rathinam.toastmasters.modules.meetingrole.exception;

public class DuplicateRoleDefinitionException extends RuntimeException {
    public DuplicateRoleDefinitionException(String name) {
        super("A role definition with name '" + name + "' already exists");
    }
}
