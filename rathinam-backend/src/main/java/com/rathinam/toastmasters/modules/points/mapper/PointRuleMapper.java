package com.rathinam.toastmasters.modules.points.mapper;

import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.points.dto.CreatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.dto.PointRuleResponse;
import com.rathinam.toastmasters.modules.points.dto.UpdatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.entity.PointRuleEntity;
import org.springframework.stereotype.Component;

@Component
public class PointRuleMapper {

    public PointRuleEntity toEntity(CreatePointRuleRequest request, RoleDefinitionEntity roleDefinition) {
        PointRuleEntity entity = new PointRuleEntity();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPoints(request.getPoints());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        entity.setCategory(request.getCategory());
        entity.setRoleDefinition(roleDefinition);
        return entity;
    }

    public void updateEntityFromRequest(PointRuleEntity entity, UpdatePointRuleRequest request, RoleDefinitionEntity roleDefinition) {
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getPoints() != null) {
            entity.setPoints(request.getPoints());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        if (request.getCategory() != null) {
            entity.setCategory(request.getCategory());
        }
        if (roleDefinition != null || request.getRoleDefinitionId() != null) {
            entity.setRoleDefinition(roleDefinition);
        }
    }

    public PointRuleResponse toResponse(PointRuleEntity entity) {
        PointRuleResponse response = new PointRuleResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPoints(entity.getPoints());
        response.setActive(entity.isActive());
        response.setCategory(entity.getCategory());
        if (entity.getRoleDefinition() != null) {
            response.setRoleDefinitionId(entity.getRoleDefinition().getId());
            response.setRoleDefinitionName(entity.getRoleDefinition().getName());
        }
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
