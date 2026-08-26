package com.rathinam.toastmasters.modules.points.exception;

import java.util.UUID;

public class PointEventNotFoundException extends RuntimeException {
    public PointEventNotFoundException(UUID id) {
        super("Point event not found with ID: " + id);
    }
}
