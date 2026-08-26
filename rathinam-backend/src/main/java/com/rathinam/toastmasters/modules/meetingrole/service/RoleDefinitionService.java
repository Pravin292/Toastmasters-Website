package com.rathinam.toastmasters.modules.meetingrole.service;

import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleDefinitionResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.UpdateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateRoleDefinitionException;
import com.rathinam.toastmasters.modules.meetingrole.exception.RoleDefinitionNotFoundException;
import com.rathinam.toastmasters.modules.meetingrole.mapper.RoleDefinitionMapper;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleDefinitionService {

    private final RoleDefinitionRepository roleDefinitionRepository;
    private final RoleDefinitionMapper roleDefinitionMapper;

    public RoleDefinitionService(RoleDefinitionRepository roleDefinitionRepository, RoleDefinitionMapper roleDefinitionMapper) {
        this.roleDefinitionRepository = roleDefinitionRepository;
        this.roleDefinitionMapper = roleDefinitionMapper;
    }

    @Transactional
    public RoleDefinitionResponse createRoleDefinition(CreateRoleDefinitionRequest request) {
        String normalizedName = request.getName().trim();
        if (roleDefinitionRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateRoleDefinitionException(normalizedName);
        }

        RoleDefinitionEntity entity = roleDefinitionMapper.toEntity(request);
        RoleDefinitionEntity saved = roleDefinitionRepository.save(entity);
        return roleDefinitionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public RoleDefinitionResponse getRoleDefinitionById(UUID id) {
        RoleDefinitionEntity entity = roleDefinitionRepository.findById(id)
                .orElseThrow(() -> new RoleDefinitionNotFoundException(id));
        return roleDefinitionMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<RoleDefinitionResponse> getAllRoleDefinitions(Boolean activeOnly) {
        List<RoleDefinitionEntity> list = (activeOnly != null && activeOnly)
                ? roleDefinitionRepository.findByActiveTrue()
                : roleDefinitionRepository.findAll();
        return list.stream().map(roleDefinitionMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public RoleDefinitionResponse updateRoleDefinition(UUID id, UpdateRoleDefinitionRequest request) {
        RoleDefinitionEntity entity = roleDefinitionRepository.findById(id)
                .orElseThrow(() -> new RoleDefinitionNotFoundException(id));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equalsIgnoreCase(entity.getName()) && roleDefinitionRepository.existsByNameIgnoreCase(newName)) {
                throw new DuplicateRoleDefinitionException(newName);
            }
        }

        roleDefinitionMapper.updateEntityFromRequest(entity, request);
        RoleDefinitionEntity updated = roleDefinitionRepository.save(entity);
        return roleDefinitionMapper.toResponse(updated);
    }
}
