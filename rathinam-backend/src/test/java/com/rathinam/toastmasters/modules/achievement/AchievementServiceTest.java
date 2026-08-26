package com.rathinam.toastmasters.modules.achievement;

import com.rathinam.toastmasters.modules.achievement.dto.BadgeResponse;
import com.rathinam.toastmasters.modules.achievement.dto.MemberAchievementResponse;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import com.rathinam.toastmasters.modules.achievement.entity.MemberAchievementEntity;
import com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementException;
import com.rathinam.toastmasters.modules.achievement.mapper.AchievementMapper;
import com.rathinam.toastmasters.modules.achievement.repository.AchievementDefinitionRepository;
import com.rathinam.toastmasters.modules.achievement.repository.MemberAchievementRepository;
import com.rathinam.toastmasters.modules.achievement.service.AchievementService;
import com.rathinam.toastmasters.modules.meeting.repository.MeetingRepository;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private MemberAchievementRepository memberAchievementRepository;

    @Mock
    private AchievementDefinitionRepository definitionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Spy
    private AchievementMapper achievementMapper;

    @InjectMocks
    private AchievementService achievementService;

    private UUID memberId;
    private UUID achievementDefId;
    private MemberEntity member;
    private AchievementDefinitionEntity definition;
    private MemberAchievementEntity achievementEntity;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        achievementDefId = UUID.randomUUID();

        member = new MemberEntity();
        member.setId(memberId);
        member.setDisplayName("Pravin");

        definition = new AchievementDefinitionEntity();
        definition.setId(achievementDefId);
        definition.setCode("FIRST_MEETING");
        definition.setName("First Step");
        definition.setCategory(AchievementCategory.ATTENDANCE);
        definition.setActive(true);
        definition.setRepeatable(false);

        achievementEntity = new MemberAchievementEntity();
        achievementEntity.setId(UUID.randomUUID());
        achievementEntity.setMember(member);
        achievementEntity.setAchievementDefinition(definition);
        achievementEntity.setEarnedAt(OffsetDateTime.now());
    }

    @Test
    void getMemberAchievements_Success() {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberAchievementRepository.findByMemberId(memberId)).thenReturn(List.of(achievementEntity));

        List<MemberAchievementResponse> responses = achievementService.getMemberAchievements(memberId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAchievementCode()).isEqualTo("FIRST_MEETING");
    }

    @Test
    void getMemberBadges_Success() {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberAchievementRepository.findByMemberId(memberId)).thenReturn(List.of(achievementEntity));

        List<BadgeResponse> badges = achievementService.getMemberBadges(memberId);

        assertThat(badges).hasSize(1);
        assertThat(badges.get(0).getBadgeName()).isEqualTo("First Step");
    }

    @Test
    void awardAchievementManually_AlreadyEarnedNonRepeatable_ThrowsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(definitionRepository.findByCodeIgnoreCase("FIRST_MEETING")).thenReturn(Optional.of(definition));
        when(memberAchievementRepository.existsByMemberIdAndAchievementDefinitionId(memberId, achievementDefId)).thenReturn(true);

        assertThatThrownBy(() -> achievementService.awardAchievementManually(memberId, "FIRST_MEETING", null, "Manual"))
                .isInstanceOf(DuplicateAchievementException.class);
    }
}
