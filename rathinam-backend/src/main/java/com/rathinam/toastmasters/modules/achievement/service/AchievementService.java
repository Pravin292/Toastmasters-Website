package com.rathinam.toastmasters.modules.achievement.service;

import com.rathinam.toastmasters.modules.achievement.dto.BadgeResponse;
import com.rathinam.toastmasters.modules.achievement.dto.MemberAchievementResponse;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import com.rathinam.toastmasters.modules.achievement.entity.MemberAchievementEntity;
import com.rathinam.toastmasters.modules.achievement.exception.AchievementDefinitionNotFoundException;
import com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementException;
import com.rathinam.toastmasters.modules.achievement.exception.InactiveAchievementDefinitionException;
import com.rathinam.toastmasters.modules.achievement.mapper.AchievementMapper;
import com.rathinam.toastmasters.modules.achievement.repository.AchievementDefinitionRepository;
import com.rathinam.toastmasters.modules.achievement.repository.MemberAchievementRepository;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AchievementService {

    private final MemberAchievementRepository memberAchievementRepository;
    private final AchievementDefinitionRepository definitionRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final AchievementMapper achievementMapper;

    public AchievementService(MemberAchievementRepository memberAchievementRepository,
                              AchievementDefinitionRepository definitionRepository,
                              MemberRepository memberRepository,
                              MeetingRepository meetingRepository,
                              AchievementMapper achievementMapper) {
        this.memberAchievementRepository = memberAchievementRepository;
        this.definitionRepository = definitionRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.achievementMapper = achievementMapper;
    }

    @Transactional(readOnly = true)
    public List<MemberAchievementResponse> getMemberAchievements(UUID memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }

        return memberAchievementRepository.findByMemberId(memberId).stream()
                .map(achievementMapper::toMemberAchievementResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BadgeResponse> getMemberBadges(UUID memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }

        return memberAchievementRepository.findByMemberId(memberId).stream()
                .map(achievementMapper::toBadgeResponse)
                .collect(Collectors.toList());
    }

    public Optional<MemberAchievementResponse> awardAchievementManually(UUID memberId, String achievementCode, UUID meetingId, String reason) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        AchievementDefinitionEntity definition = definitionRepository.findByCodeIgnoreCase(achievementCode)
                .orElseThrow(() -> new AchievementDefinitionNotFoundException(achievementCode));

        if (Boolean.FALSE.equals(definition.getActive())) {
            throw new InactiveAchievementDefinitionException(achievementCode);
        }

        if (Boolean.FALSE.equals(definition.getRepeatable()) &&
            memberAchievementRepository.existsByMemberIdAndAchievementDefinitionId(memberId, definition.getId())) {
            throw new DuplicateAchievementException(memberId, achievementCode);
        }

        MeetingEntity meeting = null;
        if (meetingId != null) {
            meeting = meetingRepository.findById(meetingId).orElse(null);
        }

        MemberAchievementEntity entity = new MemberAchievementEntity();
        entity.setMember(member);
        entity.setAchievementDefinition(definition);
        entity.setEarnedAt(OffsetDateTime.now());
        entity.setMeeting(meeting);
        entity.setReason(reason != null ? reason : "Awarded manually by officer");

        try {
            MemberAchievementEntity saved = memberAchievementRepository.save(entity);
            return Optional.of(achievementMapper.toMemberAchievementResponse(saved));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateAchievementException(memberId, achievementCode);
        }
    }
}
