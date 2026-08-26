package com.rathinam.toastmasters.modules.achievement.exception;

import java.util.UUID;

public class AchievementDefinitionNotFoundException extends RuntimeException {
    public AchievementDefinitionNotFoundException(UUID id) {
        super("Achievement definition not found with ID: " + id);
    }

    public AchievementDefinitionNotFoundException(String code) {
        super("Achievement definition not found with code: " + code);
    }
}
