package com.rathinam.toastmasters.modules.ai.dto;

import com.rathinam.toastmasters.modules.meeting.entity.MeetingType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class MeetingSummaryResponse {

    private UUID meetingId;
    private Integer meetingNumber;
    private OffsetDateTime meetingStart;
    private String theme;
    private MeetingType meetingType;
    private String conciseSummary;
    private String attendanceInsights;
    private String roleInsights;
    private String pointsInsights;
    private String performanceInsights;
    private List<String> notableAchievements;
    private List<String> positiveHighlights;
    private List<String> constructiveRecommendations;
    private String aiProvider;
    private String aiModelUsed;
    private boolean isAiGenerated;

    public MeetingSummaryResponse() {
    }

    public UUID getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(UUID meetingId) {
        this.meetingId = meetingId;
    }

    public Integer getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(Integer meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public OffsetDateTime getMeetingStart() {
        return meetingStart;
    }

    public void setMeetingStart(OffsetDateTime meetingStart) {
        this.meetingStart = meetingStart;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

    public String getConciseSummary() {
        return conciseSummary;
    }

    public void setConciseSummary(String conciseSummary) {
        this.conciseSummary = conciseSummary;
    }

    public String getAttendanceInsights() {
        return attendanceInsights;
    }

    public void setAttendanceInsights(String attendanceInsights) {
        this.attendanceInsights = attendanceInsights;
    }

    public String getRoleInsights() {
        return roleInsights;
    }

    public void setRoleInsights(String roleInsights) {
        this.roleInsights = roleInsights;
    }

    public String getPointsInsights() {
        return pointsInsights;
    }

    public void setPointsInsights(String pointsInsights) {
        this.pointsInsights = pointsInsights;
    }

    public String getPerformanceInsights() {
        return performanceInsights;
    }

    public void setPerformanceInsights(String performanceInsights) {
        this.performanceInsights = performanceInsights;
    }

    public List<String> getNotableAchievements() {
        return notableAchievements;
    }

    public void setNotableAchievements(List<String> notableAchievements) {
        this.notableAchievements = notableAchievements;
    }

    public List<String> getPositiveHighlights() {
        return positiveHighlights;
    }

    public void setPositiveHighlights(List<String> positiveHighlights) {
        this.positiveHighlights = positiveHighlights;
    }

    public List<String> getConstructiveRecommendations() {
        return constructiveRecommendations;
    }

    public void setConstructiveRecommendations(List<String> constructiveRecommendations) {
        this.constructiveRecommendations = constructiveRecommendations;
    }

    public String getAiProvider() {
        return aiProvider;
    }

    public void setAiProvider(String aiProvider) {
        this.aiProvider = aiProvider;
    }

    public String getAiModelUsed() {
        return aiModelUsed;
    }

    public void setAiModelUsed(String aiModelUsed) {
        this.aiModelUsed = aiModelUsed;
    }

    public boolean isAiGenerated() {
        return isAiGenerated;
    }

    public void setAiGenerated(boolean aiGenerated) {
        isAiGenerated = aiGenerated;
    }
}
