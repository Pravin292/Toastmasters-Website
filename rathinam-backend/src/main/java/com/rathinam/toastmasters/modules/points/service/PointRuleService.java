package com.rathinam.toastmasters.modules.points.service;

import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.meetingrole.exception.RoleDefinitionNotFoundException;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import com.rathinam.toastmasters.modules.points.dto.CreatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.dto.PointRuleResponse;
import com.rathinam.toastmasters.modules.points.dto.UpdatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.entity.PointRuleEntity;
import com.rathinam.toastmasters.modules.points.exception.DuplicatePointRuleException;
import com.rathinam.toastmasters.modules.points.exception.PointRuleNotFoundException;
import com.rathinam.toastmasters.modules.points.mapper.PointRuleMapper;
import com.rathinam.toastmasters.modules.points.repository.PointRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PointRuleService {

    private final PointRuleRepository pointRuleRepository;
    private final RoleDefinitionRepository roleDefinitionRepository;
    private final PointRuleMapper pointRuleMapper;

    public PointRuleService(PointRuleRepository pointRuleRepository,
                            RoleDefinitionRepository roleDefinitionRepository,
                            PointRuleMapper pointRuleMapper) {
        this.pointRuleRepository = pointRuleRepository;
        this.roleDefinitionRepository = roleDefinitionRepository;
        this.pointRuleMapper = pointRuleMapper;
    }

    @Transactional
    public PointRuleResponse createPointRule(CreatePointRuleRequest request) {
        String normalizedCode = request.getCode().trim().toUpperCase();
        if (pointRuleRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new DuplicatePointRuleException(normalizedCode);
        }

        RoleDefinitionEntity roleDef = null;
        if (request.getRoleDefinitionId() != null) {
            roleDef = roleDefinitionRepository.findById(request.getRoleDefinitionId())
                    .orElseThrow(() -> new RoleDefinitionNotFoundException(request.getRoleDefinitionId()));
        }

        PointRuleEntity entity = pointRuleMapper.toEntity(request, roleDef);
        entity.setCode(normalizedCode);
        PointRuleEntity saved = pointRuleRepository.save(entity);
        return pointRuleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PointRuleResponse getPointRuleById(UUID id) {
        PointRuleEntity entity = pointRuleRepository.findById(id)
                .orElseThrow(() -> new PointRuleNotFoundException(id));
        return pointRuleMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<PointRuleResponse> getAllPointRules(Boolean activeOnly) {
        List<PointRuleEntity> rules = (activeOnly != null && activeOnly)
                ? pointRuleRepository.findByActiveTrue()
                : pointRuleRepository.findAll();
        return rules.stream().map(pointRuleMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PointRuleResponse updatePointRule(UUID id, UpdatePointRuleRequest request) {
        PointRuleEntity entity = pointRuleRepository.findById(id)
                .orElseThrow(() -> new PointRuleNotFoundException(id));

        RoleDefinitionEntity roleDef = entity.getRoleDefinition();
        if (request.getRoleDefinitionId() != null) {
            roleDef = roleDefinitionRepository.findById(request.getRoleDefinitionId())
                    .orElseThrow(() -> new RoleDefinitionNotFoundException(request.getRoleDefinitionId()));
        }

        pointRuleMapper.updateEntityFromRequest(entity, request, roleDef);
        PointRuleEntity updated = pointRuleRepository.save(entity);
        return pointRuleMapper.toResponse(updated);
    }
}
