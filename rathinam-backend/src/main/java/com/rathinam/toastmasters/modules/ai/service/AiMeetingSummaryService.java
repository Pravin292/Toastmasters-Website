package com.rathinam.toastmasters.modules.ai.service;

import com.rathinam.toastmasters.modules.ai.dto.GenerateMeetingSummaryRequest;
import com.rathinam.toastmasters.modules.ai.dto.MeetingSummaryResponse;
import com.rathinam.toastmasters.modules.ai.provider.AiProvider;
import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.service.AnalyticsService;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.meetingrole.entity.MeetingRoleAssignmentEntity;
import com.rathinam.toastmasters.modules.meetingrole.repository.MeetingRoleAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AiMeetingSummaryService {

    private final AiProvider aiProvider;
    private final MeetingRepository meetingRepository;
    private final AnalyticsService analyticsService;
    private final MeetingRoleAssignmentRepository roleAssignmentRepository;

    public AiMeetingSummaryService(
            AiProvider aiProvider,
            MeetingRepository meetingRepository,
            AnalyticsService analyticsService,
            MeetingRoleAssignmentRepository roleAssignmentRepository) {
        this.aiProvider = aiProvider;
        this.meetingRepository = meetingRepository;
        this.analyticsService = analyticsService;
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    public MeetingSummaryResponse generateMeetingSummary(UUID meetingId, GenerateMeetingSummaryRequest request) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));

        MeetingAnalyticsResponse analytics = analyticsService.getMeetingAnalytics(meetingId);
        List<MeetingRoleAssignmentEntity> roleAssignments = roleAssignmentRepository.findByMeetingId(meetingId);

        String prompt = buildPrompt(meeting, analytics, roleAssignments, request);
        String aiGeneratedText = aiProvider.generate(prompt);

        return buildResponse(meeting, analytics, roleAssignments, aiGeneratedText);
    }

    private String buildPrompt(MeetingEntity meeting,
                               MeetingAnalyticsResponse analytics,
                               List<MeetingRoleAssignmentEntity> roleAssignments,
                               GenerateMeetingSummaryRequest request) {
        String roleDetails = roleAssignments.stream()
                .map(ra -> ra.getRoleDefinition().getName() + ": " + ra.getMember().getDisplayName())
                .collect(Collectors.joining(", "));
        if (roleDetails.isEmpty()) {
            roleDetails = "None assigned";
        }

        String focusArea = request != null && request.getFocusArea() != null && !request.getFocusArea().isBlank()
                ? request.getFocusArea().trim()
                : "General meeting summary and member participation";

        String tone = request != null && request.getTone() != null && !request.getTone().isBlank()
                ? request.getTone().trim()
                : "Professional, encouraging, and constructive";

        return String.format("""
                You are an AI assistant for the Rathinam Toastmasters Digital Platform.
                Analyze the following verified backend data for Meeting #%d and provide a summary.

                [MEETING METADATA]
                - Meeting Number: #%d
                - Meeting Date/Time: %s
                - Theme: "%s"
                - Meeting Type: %s
                - Meeting Status: %s

                [ATTENDANCE & PARTICIPATION METRICS]
                - Total Attendance Records: %d
                - Present Members Count: %d
                - Absent Members Count: %d
                - Excused Members Count: %d
                - Attendance Rate: %.2f%%
                - Participating Members Count: %d

                [ROLES & POINTS]
                - Roles Filled: %d
                - Roles Remaining: %d
                - Assigned Roles & Members: %s
                - Total Points Awarded: %d

                [PROMPT INSTRUCTIONS]
                1. Focus Area: %s
                2. Tone: %s
                3. STRICT RULE: Rely ONLY on the verified data provided above. Never invent or fabricate members, roles, attendance, or points.
                4. Provide a clear, professional meeting summary highlighting member participation, role execution, and points earned.
                """,
                meeting.getMeetingNumber(),
                meeting.getMeetingNumber(),
                meeting.getMeetingStart(),
                meeting.getTheme() != null ? meeting.getTheme() : "General Meeting",
                meeting.getMeetingType(),
                meeting.getStatus(),
                analytics.getTotalAttendanceRecords(),
                analytics.getPresentCount(),
                analytics.getAbsentCount(),
                analytics.getExcusedCount(),
                analytics.getAttendancePercentage(),
                analytics.getParticipatingMembersCount(),
                analytics.getRolesFilled(),
                analytics.getRolesRemaining(),
                roleDetails,
                analytics.getTotalPointsAwarded(),
                focusArea,
                tone
        );
    }

    private MeetingSummaryResponse buildResponse(MeetingEntity meeting,
                                                  MeetingAnalyticsResponse analytics,
                                                  List<MeetingRoleAssignmentEntity> roleAssignments,
                                                  String aiGeneratedText) {
        MeetingSummaryResponse response = new MeetingSummaryResponse();
        response.setMeetingId(meeting.getId());
        response.setMeetingNumber(meeting.getMeetingNumber());
        response.setMeetingStart(meeting.getMeetingStart());
        response.setTheme(meeting.getTheme());
        response.setMeetingType(meeting.getMeetingType());

        response.setConciseSummary(aiGeneratedText);

        response.setAttendanceInsights(String.format("Meeting #%d recorded a %.2f%% attendance rate (%d present, %d absent, %d excused).",
                meeting.getMeetingNumber(), analytics.getAttendancePercentage(), analytics.getPresentCount(), analytics.getAbsentCount(), analytics.getExcusedCount()));

        response.setRoleInsights(String.format("%d meeting roles were successfully filled out of %d assigned roles.",
                analytics.getRolesFilled(), analytics.getRolesAssigned()));

        response.setPointsInsights(String.format("A total of %d points were awarded across meeting activities and participation.",
                analytics.getTotalPointsAwarded()));

        response.setPerformanceInsights(String.format("%d members actively participated in Meeting #%d.",
                analytics.getParticipatingMembersCount(), meeting.getMeetingNumber()));

        List<String> notableAchievements = new ArrayList<>();
        if (!roleAssignments.isEmpty()) {
            notableAchievements.add(String.format("%d members performed critical meeting roles.", roleAssignments.size()));
        }
        if (analytics.getPresentCount() > 0) {
            notableAchievements.add(String.format("%d members demonstrated commitment by attending.", analytics.getPresentCount()));
        }
        response.setNotableAchievements(notableAchievements);

        List<String> positiveHighlights = List.of(
                String.format("Active participation with %d attending members.", analytics.getPresentCount()),
                String.format("%d total points awarded to high performers.", analytics.getTotalPointsAwarded())
        );
        response.setPositiveHighlights(positiveHighlights);

        List<String> recommendations = new ArrayList<>();
        if (analytics.getRolesRemaining() > 0) {
            recommendations.add(String.format("Assign the remaining %d unfilled meeting roles in advance for upcoming meetings.", analytics.getRolesRemaining()));
        } else {
            recommendations.add("Maintain full role coverage for upcoming Toastmasters sessions.");
        }
        response.setConstructiveRecommendations(recommendations);

        response.setAiProvider(aiProvider.getProviderName());
        response.setAiModelUsed(aiProvider.getModelName());
        response.setAiGenerated(true);

        return response;
    }
}
