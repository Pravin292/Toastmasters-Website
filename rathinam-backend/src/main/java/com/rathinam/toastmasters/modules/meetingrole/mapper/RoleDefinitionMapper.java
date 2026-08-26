package com.rathinam.toastmasters.modules.meetingrole.mapper;

import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleDefinitionResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.UpdateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleDefinitionMapper {

    public RoleDefinitionEntity toEntity(CreateRoleDefinitionRequest request) {
        RoleDefinitionEntity entity = new RoleDefinitionEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        return entity;
    }

    public void updateEntityFromRequest(RoleDefinitionEntity entity, UpdateRoleDefinitionRequest request) {
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }

    public RoleDefinitionResponse toResponse(RoleDefinitionEntity entity) {
        RoleDefinitionResponse response = new RoleDefinitionResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setActive(entity.isActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
