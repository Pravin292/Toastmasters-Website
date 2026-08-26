package com.rathinam.toastmasters.modules.achievement.exception;

public class DuplicateAchievementDefinitionException extends RuntimeException {
    public DuplicateAchievementDefinitionException(String code) {
        super("An achievement definition with code '" + code + "' already exists");
    }
}
