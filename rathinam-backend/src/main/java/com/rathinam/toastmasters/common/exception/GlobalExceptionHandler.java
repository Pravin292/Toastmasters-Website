package com.rathinam.toastmasters.common.exception;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.member.exception.DuplicateEmailException;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberNotFoundException(MemberNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmailException(DuplicateEmailException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMeetingNotFoundException(com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meeting.exception.DuplicateMeetingNumberException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateMeetingNumberException(com.rathinam.toastmasters.modules.meeting.exception.DuplicateMeetingNumberException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meeting.exception.InvalidMeetingStatusTransitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidMeetingStatusTransitionException(com.rathinam.toastmasters.modules.meeting.exception.InvalidMeetingStatusTransitionException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.notification.exception.NotificationNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotificationNotFoundException(com.rathinam.toastmasters.modules.notification.exception.NotificationNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.attendance.exception.AttendanceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleAttendanceNotFoundException(com.rathinam.toastmasters.modules.attendance.exception.AttendanceNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.attendance.exception.DuplicateAttendanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateAttendanceException(com.rathinam.toastmasters.modules.attendance.exception.DuplicateAttendanceException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        ApiResponse<Map<String, String>> response = new ApiResponse<>(false, errors, "Validation failed");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meetingrole.exception.RoleDefinitionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRoleDefinitionNotFoundException(com.rathinam.toastmasters.modules.meetingrole.exception.RoleDefinitionNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateRoleDefinitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateRoleDefinitionException(com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateRoleDefinitionException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meetingrole.exception.MeetingRoleAssignmentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMeetingRoleAssignmentNotFoundException(com.rathinam.toastmasters.modules.meetingrole.exception.MeetingRoleAssignmentNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateMeetingRoleAssignmentException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateMeetingRoleAssignmentException(com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateMeetingRoleAssignmentException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.meetingrole.exception.InactiveRoleDefinitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInactiveRoleDefinitionException(com.rathinam.toastmasters.modules.meetingrole.exception.InactiveRoleDefinitionException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.points.exception.PointRuleNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePointRuleNotFoundException(com.rathinam.toastmasters.modules.points.exception.PointRuleNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.points.exception.DuplicatePointRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicatePointRuleException(com.rathinam.toastmasters.modules.points.exception.DuplicatePointRuleException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.points.exception.PointEventNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePointEventNotFoundException(com.rathinam.toastmasters.modules.points.exception.PointEventNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.points.exception.DuplicatePointEventException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicatePointEventException(com.rathinam.toastmasters.modules.points.exception.DuplicatePointEventException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.points.exception.InvalidPointAwardException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPointAwardException(com.rathinam.toastmasters.modules.points.exception.InvalidPointAwardException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.ranking.exception.InvalidRankingPeriodException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRankingPeriodException(com.rathinam.toastmasters.modules.ranking.exception.InvalidRankingPeriodException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.achievement.exception.AchievementDefinitionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleAchievementDefinitionNotFoundException(com.rathinam.toastmasters.modules.achievement.exception.AchievementDefinitionNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementDefinitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateAchievementDefinitionException(com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementDefinitionException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateAchievementException(com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.achievement.exception.InactiveAchievementDefinitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInactiveAchievementDefinitionException(com.rathinam.toastmasters.modules.achievement.exception.InactiveAchievementDefinitionException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.certificate.exception.CertificateNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCertificateNotFoundException(com.rathinam.toastmasters.modules.certificate.exception.CertificateNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.certificate.exception.DuplicateCertificateException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateCertificateException(com.rathinam.toastmasters.modules.certificate.exception.DuplicateCertificateException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.ai.exception.AiConfigurationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiConfigurationException(com.rathinam.toastmasters.modules.ai.exception.AiConfigurationException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.ai.exception.AiQuotaExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiQuotaExceededException(com.rathinam.toastmasters.modules.ai.exception.AiQuotaExceededException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(com.rathinam.toastmasters.modules.ai.exception.AiServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiServiceUnavailableException(com.rathinam.toastmasters.modules.ai.exception.AiServiceUnavailableException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        ApiResponse<Void> response = ApiResponse.error("Access denied: You do not have permission to perform this action");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException ex) {
        ApiResponse<Void> response = ApiResponse.error("Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled server exception:", ex);
        ApiResponse<Void> response = ApiResponse.error("An unexpected internal error occurred. Please try again later.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

