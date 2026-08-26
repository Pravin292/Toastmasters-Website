package com.rathinam.toastmasters.modules.meeting.service;

import com.rathinam.toastmasters.modules.analytics.dto.MeetingAnalyticsResponse;
import com.rathinam.toastmasters.modules.analytics.service.AnalyticsService;
import com.rathinam.toastmasters.modules.meeting.dto.CreateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingResponse;
import com.rathinam.toastmasters.modules.meeting.dto.MeetingWorkflowResponse;
import com.rathinam.toastmasters.modules.meeting.dto.UpdateMeetingRequest;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingStatus;
import com.rathinam.toastmasters.modules.meeting.exception.DuplicateMeetingNumberException;
import com.rathinam.toastmasters.modules.meeting.exception.InvalidMeetingStatusTransitionException;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.mapper.MeetingMapper;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;
    private final AnalyticsService analyticsService;

    public MeetingService(MeetingRepository meetingRepository,
                          MeetingMapper meetingMapper,
                          AnalyticsService analyticsService) {
        this.meetingRepository = meetingRepository;
        this.meetingMapper = meetingMapper;
        this.analyticsService = analyticsService;
    }

    @Transactional
    public MeetingResponse createMeeting(CreateMeetingRequest request) {
        if (meetingRepository.existsByMeetingNumber(request.getMeetingNumber())) {
            throw new DuplicateMeetingNumberException(request.getMeetingNumber());
        }

        MeetingEntity entity = meetingMapper.toEntity(request);
        MeetingEntity savedEntity = meetingRepository.save(entity);
        return meetingMapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public MeetingResponse getMeetingById(UUID id) {
        MeetingEntity entity = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(id));
        return meetingMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<MeetingResponse> getMeetings(Pageable pageable) {
        return meetingRepository.findAll(pageable)
                .map(meetingMapper::toResponse);
    }

    @Transactional
    public MeetingResponse updateMeeting(UUID id, UpdateMeetingRequest request) {
        MeetingEntity entity = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(id));

        if (request.getMeetingNumber() != null && !request.getMeetingNumber().equals(entity.getMeetingNumber())) {
            if (meetingRepository.existsByMeetingNumber(request.getMeetingNumber())) {
                throw new DuplicateMeetingNumberException(request.getMeetingNumber());
            }
        }

        if (request.getStatus() != null && !request.getStatus().equals(entity.getStatus())) {
            validateStatusTransition(entity.getStatus(), request.getStatus());
        }

        meetingMapper.updateEntityFromRequest(entity, request);
        MeetingEntity updatedEntity = meetingRepository.save(entity);
        return meetingMapper.toResponse(updatedEntity);
    }

    @Transactional
    public MeetingResponse startMeeting(UUID id) {
        MeetingEntity entity = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(id));

        validateStatusTransition(entity.getStatus(), MeetingStatus.IN_PROGRESS);

        entity.setStatus(MeetingStatus.IN_PROGRESS);
        MeetingEntity saved = meetingRepository.save(entity);
        return meetingMapper.toResponse(saved);
    }

    @Transactional
    public MeetingResponse completeMeeting(UUID id) {
        MeetingEntity entity = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(id));

        validateStatusTransition(entity.getStatus(), MeetingStatus.COMPLETED);

        entity.setStatus(MeetingStatus.COMPLETED);
        MeetingEntity saved = meetingRepository.save(entity);
        return meetingMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MeetingWorkflowResponse getMeetingWorkflow(UUID id) {
        MeetingEntity entity = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(id));

        MeetingAnalyticsResponse analytics = analyticsService.getMeetingAnalytics(id);

        List<String> warnings = new ArrayList<>();
        if (analytics.getTotalAttendanceRecords() == 0) {
            warnings.add("No attendance records have been recorded for this meeting.");
        }
        if (analytics.getRolesAssigned() == 0) {
            warnings.add("No meeting roles have been assigned for this meeting.");
        } else if (analytics.getRolesRemaining() > 0) {
            warnings.add(String.format("%d configured meeting roles remain unassigned.", analytics.getRolesRemaining()));
        }

        MeetingWorkflowResponse response = new MeetingWorkflowResponse();
        response.setMeetingId(entity.getId());
        response.setMeetingNumber(entity.getMeetingNumber());
        response.setMeetingStart(entity.getMeetingStart());
        response.setTheme(entity.getTheme());
        response.setMeetingType(entity.getMeetingType());
        response.setStatus(entity.getStatus());

        response.setCanStart(entity.getStatus() == MeetingStatus.SCHEDULED);
        response.setCanComplete(entity.getStatus() == MeetingStatus.IN_PROGRESS);

        response.setAttendanceSummary(new MeetingWorkflowResponse.AttendanceWorkflowSummary(
                analytics.getTotalAttendanceRecords(),
                analytics.getPresentCount(),
                analytics.getAbsentCount(),
                analytics.getExcusedCount(),
                analytics.getAttendancePercentage()
        ));

        response.setRoleSummary(new MeetingWorkflowResponse.RoleWorkflowSummary(
                analytics.getRolesAssigned(),
                analytics.getRolesFilled(),
                analytics.getRolesRemaining()
        ));

        response.setPointsSummary(new MeetingWorkflowResponse.PointsWorkflowSummary(
                analytics.getTotalPointsAwarded()
        ));

        response.setWorkflowWarnings(warnings);
        response.setAiSummaryAvailable(entity.getStatus() == MeetingStatus.COMPLETED || entity.getStatus() == MeetingStatus.IN_PROGRESS);

        return response;
    }

    public void validateStatusTransition(MeetingStatus currentStatus, MeetingStatus targetStatus) {
        if (currentStatus == targetStatus) {
            return;
        }

        switch (currentStatus) {
            case SCHEDULED -> {
                if (targetStatus != MeetingStatus.IN_PROGRESS && targetStatus != MeetingStatus.CANCELLED) {
                    throw new InvalidMeetingStatusTransitionException(
                            String.format("Cannot transition meeting from SCHEDULED directly to %s. A meeting must be started before completing.", targetStatus)
                    );
                }
            }
            case IN_PROGRESS -> {
                if (targetStatus != MeetingStatus.COMPLETED && targetStatus != MeetingStatus.CANCELLED) {
                    throw new InvalidMeetingStatusTransitionException(currentStatus, targetStatus);
                }
            }
            case COMPLETED -> throw new InvalidMeetingStatusTransitionException(
                    "Cannot modify status of a COMPLETED meeting."
            );
            case CANCELLED -> throw new InvalidMeetingStatusTransitionException(
                    "Cannot modify status of a CANCELLED meeting."
            );
        }
    }
}

