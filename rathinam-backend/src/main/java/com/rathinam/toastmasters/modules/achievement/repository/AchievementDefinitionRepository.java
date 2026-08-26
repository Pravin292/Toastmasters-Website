package com.rathinam.toastmasters.modules.achievement.repository;

import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementDefinitionRepository extends JpaRepository<AchievementDefinitionEntity, UUID> {
    Optional<AchievementDefinitionEntity> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    List<AchievementDefinitionEntity> findByIsActiveTrue();
    List<AchievementDefinitionEntity> findByCategory(AchievementCategory category);
}
