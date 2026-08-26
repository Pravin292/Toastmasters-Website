package com.rathinam.toastmasters.modules.points.repository;

import com.rathinam.toastmasters.modules.points.entity.PointRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PointRuleRepository extends JpaRepository<PointRuleEntity, UUID> {
    boolean existsByCodeIgnoreCase(String code);
    Optional<PointRuleEntity> findByCodeIgnoreCase(String code);
    Optional<PointRuleEntity> findByRoleDefinitionIdAndActiveTrue(UUID roleDefinitionId);
    List<PointRuleEntity> findByActiveTrue();
}
