package com.rathinam.toastmasters.modules.achievement.exception;

import java.util.UUID;

public class DuplicateAchievementException extends RuntimeException {
    public DuplicateAchievementException(UUID memberId, String achievementCode) {
        super("Member with ID '" + memberId + "' has already earned achievement '" + achievementCode + "'");
    }
}
