package com.rathinam.toastmasters.modules.points;

import com.rathinam.toastmasters.modules.meetingrole.entity.RoleDefinitionEntity;
import com.rathinam.toastmasters.modules.meetingrole.repository.RoleDefinitionRepository;
import com.rathinam.toastmasters.modules.points.dto.CreatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.dto.PointRuleResponse;
import com.rathinam.toastmasters.modules.points.dto.UpdatePointRuleRequest;
import com.rathinam.toastmasters.modules.points.entity.PointRuleCategory;
import com.rathinam.toastmasters.modules.points.entity.PointRuleEntity;
import com.rathinam.toastmasters.modules.points.exception.DuplicatePointRuleException;
import com.rathinam.toastmasters.modules.points.exception.PointRuleNotFoundException;
import com.rathinam.toastmasters.modules.points.mapper.PointRuleMapper;
import com.rathinam.toastmasters.modules.points.repository.PointRuleRepository;
import com.rathinam.toastmasters.modules.points.service.PointRuleService;
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
class PointRuleServiceTest {

    @Mock
    private PointRuleRepository pointRuleRepository;

    @Mock
    private RoleDefinitionRepository roleDefinitionRepository;

    @Spy
    private PointRuleMapper pointRuleMapper;

    @InjectMocks
    private PointRuleService pointRuleService;

    private UUID ruleId;
    private PointRuleEntity ruleEntity;

    @BeforeEach
    void setUp() {
        ruleId = UUID.randomUUID();
        ruleEntity = new PointRuleEntity();
        ruleEntity.setId(ruleId);
        ruleEntity.setCode("ATTENDANCE_PRESENT");
        ruleEntity.setName("Attendance Present");
        ruleEntity.setPoints(5);
        ruleEntity.setActive(true);
        ruleEntity.setCategory(PointRuleCategory.ATTENDANCE);
    }

    @Test
    void createPointRule_Success() {
        CreatePointRuleRequest request = new CreatePointRuleRequest("ATTENDANCE_PRESENT", "Attendance Present", 5, PointRuleCategory.ATTENDANCE);
        when(pointRuleRepository.existsByCodeIgnoreCase("ATTENDANCE_PRESENT")).thenReturn(false);
        when(pointRuleRepository.save(any(PointRuleEntity.class))).thenReturn(ruleEntity);

        PointRuleResponse response = pointRuleService.createPointRule(request);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("ATTENDANCE_PRESENT");
        assertThat(response.getPoints()).isEqualTo(5);
    }

    @Test
    void createPointRule_Duplicate_ThrowsException() {
        CreatePointRuleRequest request = new CreatePointRuleRequest("ATTENDANCE_PRESENT", "Attendance Present", 5, PointRuleCategory.ATTENDANCE);
        when(pointRuleRepository.existsByCodeIgnoreCase("ATTENDANCE_PRESENT")).thenReturn(true);

        assertThatThrownBy(() -> pointRuleService.createPointRule(request))
                .isInstanceOf(DuplicatePointRuleException.class);
    }

    @Test
    void getPointRuleById_Success() {
        when(pointRuleRepository.findById(ruleId)).thenReturn(Optional.of(ruleEntity));

        PointRuleResponse response = pointRuleService.getPointRuleById(ruleId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(ruleId);
    }

    @Test
    void getPointRuleById_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        when(pointRuleRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointRuleService.getPointRuleById(unknownId))
                .isInstanceOf(PointRuleNotFoundException.class);
    }

    @Test
    void updatePointRule_Deactivate_Success() {
        UpdatePointRuleRequest request = new UpdatePointRuleRequest();
        request.setActive(false);

        when(pointRuleRepository.findById(ruleId)).thenReturn(Optional.of(ruleEntity));
        when(pointRuleRepository.save(any(PointRuleEntity.class))).thenAnswer(i -> i.getArgument(0));

        PointRuleResponse response = pointRuleService.updatePointRule(ruleId, request);

        assertThat(response).isNotNull();
        assertThat(response.isActive()).isFalse();
    }
}
