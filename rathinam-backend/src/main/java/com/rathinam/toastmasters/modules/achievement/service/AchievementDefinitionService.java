package com.rathinam.toastmasters.modules.achievement.service;

import com.rathinam.toastmasters.modules.achievement.dto.AchievementDefinitionResponse;
import com.rathinam.toastmasters.modules.achievement.dto.CreateAchievementDefinitionRequest;
import com.rathinam.toastmasters.modules.achievement.dto.UpdateAchievementDefinitionRequest;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import com.rathinam.toastmasters.modules.achievement.exception.AchievementDefinitionNotFoundException;
import com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementDefinitionException;
import com.rathinam.toastmasters.modules.achievement.mapper.AchievementMapper;
import com.rathinam.toastmasters.modules.achievement.repository.AchievementDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AchievementDefinitionService {

    private final AchievementDefinitionRepository definitionRepository;
    private final AchievementMapper achievementMapper;

    public AchievementDefinitionService(AchievementDefinitionRepository definitionRepository,
                                         AchievementMapper achievementMapper) {
        this.definitionRepository = definitionRepository;
        this.achievementMapper = achievementMapper;
    }

    public AchievementDefinitionResponse createAchievementDefinition(CreateAchievementDefinitionRequest request) {
        if (definitionRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateAchievementDefinitionException(request.getCode());
        }

        AchievementDefinitionEntity entity = new AchievementDefinitionEntity();
        entity.setCode(request.getCode().trim().toUpperCase());
        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription().trim());
        entity.setIcon(request.getIcon().trim());
        entity.setCategory(request.getCategory());
        entity.setCriteriaType(request.getCriteriaType());
        entity.setCriteriaThreshold(request.getCriteriaThreshold());
        entity.setRepeatable(request.getRepeatable() != null ? request.getRepeatable() : false);
        entity.setActive(true);

        AchievementDefinitionEntity saved = definitionRepository.save(entity);
        return achievementMapper.toDefinitionResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AchievementDefinitionResponse> getAllAchievementDefinitions(Boolean activeOnly) {
        List<AchievementDefinitionEntity> entities = Boolean.TRUE.equals(activeOnly)
                ? definitionRepository.findByIsActiveTrue()
                : definitionRepository.findAll();

        return entities.stream()
                .map(achievementMapper::toDefinitionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AchievementDefinitionResponse getAchievementDefinitionById(UUID id) {
        AchievementDefinitionEntity entity = definitionRepository.findById(id)
                .orElseThrow(() -> new AchievementDefinitionNotFoundException(id));
        return achievementMapper.toDefinitionResponse(entity);
    }

    public AchievementDefinitionResponse updateAchievementDefinition(UUID id, UpdateAchievementDefinitionRequest request) {
        AchievementDefinitionEntity entity = definitionRepository.findById(id)
                .orElseThrow(() -> new AchievementDefinitionNotFoundException(id));

        if (request.getName() != null) {
            entity.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription().trim());
        }
        if (request.getIcon() != null) {
            entity.setIcon(request.getIcon().trim());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        if (request.getRepeatable() != null) {
            entity.setRepeatable(request.getRepeatable());
        }

        AchievementDefinitionEntity updated = definitionRepository.save(entity);
        return achievementMapper.toDefinitionResponse(updated);
    }
}
