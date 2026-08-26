package com.rathinam.toastmasters.modules.meetingrole.repository;

import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleDefinitionRepository extends JpaRepository<RoleDefinitionEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);
    Optional<RoleDefinitionEntity> findByNameIgnoreCase(String name);
    List<RoleDefinitionEntity> findByActiveTrue();
    long countByActiveTrue();
}
