package com.rathinam.toastmasters.modules.meetingrole;

import com.rathinam.toastmasters.modules.meetingrole.dto.CreateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.dto.RoleDefinitionResponse;
import com.rathinam.toastmasters.modules.meetingrole.dto.UpdateRoleDefinitionRequest;
import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.meetingrole.exception.DuplicateRoleDefinitionException;
import com.rathinam.toastmasters.modules.meetingrole.exception.RoleDefinitionNotFoundException;
import com.rathinam.toastmasters.modules.meetingrole.mapper.RoleDefinitionMapper;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import com.rathinam.toastmasters.modules.meetingrole.service.RoleDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleDefinitionServiceTest {

    @Mock
    private RoleDefinitionRepository roleDefinitionRepository;

    @Spy
    private RoleDefinitionMapper roleDefinitionMapper;

    @InjectMocks
    private RoleDefinitionService roleDefinitionService;

    private UUID roleId;
    private RoleDefinitionEntity roleEntity;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        roleEntity = new RoleDefinitionEntity();
        roleEntity.setId(roleId);
        roleEntity.setName("Timer");
        roleEntity.setDescription("Keeps track of time");
        roleEntity.setActive(true);
    }

    @Test
    void createRoleDefinition_Success() {
        CreateRoleDefinitionRequest request = new CreateRoleDefinitionRequest("Timer", "Keeps track of time");
        when(roleDefinitionRepository.existsByNameIgnoreCase("Timer")).thenReturn(false);
        when(roleDefinitionRepository.save(any(RoleDefinitionEntity.class))).thenReturn(roleEntity);

        RoleDefinitionResponse response = roleDefinitionService.createRoleDefinition(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Timer");
        assertThat(response.isActive()).isTrue();
    }

    @Test
    void createRoleDefinition_Duplicate_ThrowsException() {
        CreateRoleDefinitionRequest request = new CreateRoleDefinitionRequest("Timer", "Keeps track of time");
        when(roleDefinitionRepository.existsByNameIgnoreCase("Timer")).thenReturn(true);

        assertThatThrownBy(() -> roleDefinitionService.createRoleDefinition(request))
                .isInstanceOf(DuplicateRoleDefinitionException.class);
    }

    @Test
    void getRoleDefinitionById_Success() {
        when(roleDefinitionRepository.findById(roleId)).thenReturn(Optional.of(roleEntity));

        RoleDefinitionResponse response = roleDefinitionService.getRoleDefinitionById(roleId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(roleId);
    }

    @Test
    void getRoleDefinitionById_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        when(roleDefinitionRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleDefinitionService.getRoleDefinitionById(unknownId))
                .isInstanceOf(RoleDefinitionNotFoundException.class);
    }

    @Test
    void getAllRoleDefinitions_ActiveOnly_Success() {
        when(roleDefinitionRepository.findByActiveTrue()).thenReturn(List.of(roleEntity));

        List<RoleDefinitionResponse> result = roleDefinitionService.getAllRoleDefinitions(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Timer");
    }

    @Test
    void updateRoleDefinition_Deactivate_Success() {
        UpdateRoleDefinitionRequest request = new UpdateRoleDefinitionRequest();
        request.setActive(false);

        when(roleDefinitionRepository.findById(roleId)).thenReturn(Optional.of(roleEntity));
        when(roleDefinitionRepository.save(any(RoleDefinitionEntity.class))).thenAnswer(i -> i.getArgument(0));

        RoleDefinitionResponse response = roleDefinitionService.updateRoleDefinition(roleId, request);

        assertThat(response).isNotNull();
        assertThat(response.isActive()).isFalse();
    }
}
