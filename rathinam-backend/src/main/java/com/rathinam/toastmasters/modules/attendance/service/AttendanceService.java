package com.rathinam.toastmasters.modules.attendance.service;

import com.rathinam.toastmasters.modules.attendance.dto.AttendanceResponse;
import com.rathinam.toastmasters.modules.attendance.dto.CreateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.dto.UpdateAttendanceRequest;
import com.rathinam.toastmasters.modules.attendance.entity.AttendanceEntity;
import com.rathinam.toastmasters.modules.attendance.exception.AttendanceNotFoundException;
import com.rathinam.toastmasters.modules.attendance.exception.DuplicateAttendanceException;
import com.rathinam.toastmasters.modules.attendance.mapper.AttendanceMapper;
import com.rathinam.toastmasters.modules.attendance.repository.AttendanceRepository;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.exception.MeetingNotFoundException;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.points.service.PointAwardService;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;
    private final AttendanceMapper attendanceMapper;
    private final PointAwardService pointAwardService;
    private final AchievementEvaluationService achievementEvaluationService;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             MeetingRepository meetingRepository,
                             MemberRepository memberRepository,
                             AttendanceMapper attendanceMapper,
                             PointAwardService pointAwardService,
                             AchievementEvaluationService achievementEvaluationService) {
        this.attendanceRepository = attendanceRepository;
        this.meetingRepository = meetingRepository;
        this.memberRepository = memberRepository;
        this.attendanceMapper = attendanceMapper;
        this.pointAwardService = pointAwardService;
        this.achievementEvaluationService = achievementEvaluationService;
    }

    @Transactional
    public AttendanceResponse recordAttendance(UUID meetingId, CreateAttendanceRequest request) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));

        MemberEntity member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));

        if (attendanceRepository.existsByMeetingIdAndMemberId(meetingId, request.getMemberId())) {
            throw new DuplicateAttendanceException(meetingId, request.getMemberId());
        }

        AttendanceEntity entity = attendanceMapper.toEntity(request, meeting, member);
        AttendanceEntity savedEntity = attendanceRepository.save(entity);

        // Automatically award points for attendance
        pointAwardService.awardPointsForAttendance(savedEntity);

        // Evaluate member achievements
        achievementEvaluationService.evaluateMemberAchievements(savedEntity.getMember().getId());

        return attendanceMapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMeetingAttendance(UUID meetingId) {
        if (!meetingRepository.existsById(meetingId)) {
            throw new MeetingNotFoundException(meetingId);
        }

        return attendanceRepository.findByMeetingId(meetingId).stream()
                .map(attendanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(UUID attendanceId) {
        AttendanceEntity entity = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceNotFoundException(attendanceId));
        return attendanceMapper.toResponse(entity);
    }

    @Transactional
    public AttendanceResponse updateAttendance(UUID attendanceId, UpdateAttendanceRequest request) {
        AttendanceEntity entity = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceNotFoundException(attendanceId));

        attendanceMapper.updateEntityFromRequest(entity, request);
        AttendanceEntity updatedEntity = attendanceRepository.save(entity);
        return attendanceMapper.toResponse(updatedEntity);
    }
}
