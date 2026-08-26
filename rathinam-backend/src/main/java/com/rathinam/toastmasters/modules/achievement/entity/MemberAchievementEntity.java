package com.rathinam.toastmasters.modules.achievement.entity;

import com.rathinam.toastmasters.common.entity.BaseEntity;
import com.rathinam.toastmasters.modules.meeting.entity.MeetingEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "member_achievements")
public class MemberAchievementEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "achievement_definition_id", nullable = false)
    private AchievementDefinitionEntity achievementDefinition;

    @Column(name = "earned_at", nullable = false)
    private OffsetDateTime earnedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    @Column(columnDefinition = "TEXT")
    private String reason;

    public MemberAchievementEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    public AchievementDefinitionEntity getAchievementDefinition() {
        return achievementDefinition;
    }

    public void setAchievementDefinition(AchievementDefinitionEntity achievementDefinition) {
        this.achievementDefinition = achievementDefinition;
    }

    public OffsetDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(OffsetDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public MeetingEntity getMeeting() {
        return meeting;
    }

    public void setMeeting(MeetingEntity meeting) {
        this.meeting = meeting;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
