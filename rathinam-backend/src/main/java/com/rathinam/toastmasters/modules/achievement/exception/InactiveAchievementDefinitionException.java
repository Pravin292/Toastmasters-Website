package com.rathinam.toastmasters.modules.achievement.exception;

public class InactiveAchievementDefinitionException extends RuntimeException {
    public InactiveAchievementDefinitionException(String code) {
        super("Achievement definition with code '" + code + "' is inactive and cannot be awarded");
    }
}
