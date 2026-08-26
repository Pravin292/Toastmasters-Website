package com.rathinam.toastmasters.modules.achievement;

import com.rathinam.toastmasters.modules.achievement.dto.AchievementDefinitionResponse;
import com.rathinam.toastmasters.modules.achievement.dto.CreateAchievementDefinitionRequest;
import com.rathinam.toastmasters.modules.achievement.dto.UpdateAchievementDefinitionRequest;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCategory;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementCriteriaType;
import com.rathinam.toastmasters.modules.achievement.entity.AchievementDefinitionEntity;
import com.rathinam.toastmasters.modules.achievement.exception.DuplicateAchievementDefinitionException;
import com.rathinam.toastmasters.modules.achievement.mapper.AchievementMapper;
import com.rathinam.toastmasters.modules.achievement.repository.AchievementDefinitionRepository;
import com.rathinam.toastmasters.modules.achievement.service.AchievementDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementDefinitionServiceTest {

    @Mock
    private AchievementDefinitionRepository definitionRepository;

    @Spy
    private AchievementMapper achievementMapper;

    @InjectMocks
    private AchievementDefinitionService definitionService;

    private UUID definitionId;
    private AchievementDefinitionEntity entity;

    @BeforeEach
    void setUp() {
        definitionId = UUID.randomUUID();
        entity = new AchievementDefinitionEntity();
        entity.setId(definitionId);
        entity.setCode("FIRST_MEETING");
        entity.setName("First Step");
        entity.setDescription("Attended first meeting");
        entity.setIcon("footsteps");
        entity.setCategory(AchievementCategory.ATTENDANCE);
        entity.setCriteriaType(AchievementCriteriaType.ATTENDANCE_COUNT);
        entity.setCriteriaThreshold(1);
        entity.setActive(true);
    }

    @Test
    void createAchievementDefinition_Success() {
        CreateAchievementDefinitionRequest request = new CreateAchievementDefinitionRequest("FIRST_MEETING", "First Step", "Attended first meeting", "footsteps", AchievementCategory.ATTENDANCE, AchievementCriteriaType.ATTENDANCE_COUNT, 1);

        when(definitionRepository.existsByCodeIgnoreCase("FIRST_MEETING")).thenReturn(false);
        when(definitionRepository.save(any(AchievementDefinitionEntity.class))).thenReturn(entity);

        AchievementDefinitionResponse response = definitionService.createAchievementDefinition(request);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("FIRST_MEETING");
        assertThat(response.getName()).isEqualTo("First Step");
    }

    @Test
    void createAchievementDefinition_DuplicateCode_ThrowsException() {
        CreateAchievementDefinitionRequest request = new CreateAchievementDefinitionRequest("FIRST_MEETING", "First Step", "Attended first meeting", "footsteps", AchievementCategory.ATTENDANCE, AchievementCriteriaType.ATTENDANCE_COUNT, 1);

        when(definitionRepository.existsByCodeIgnoreCase("FIRST_MEETING")).thenReturn(true);

        assertThatThrownBy(() -> definitionService.createAchievementDefinition(request))
                .isInstanceOf(DuplicateAchievementDefinitionException.class);
    }

    @Test
    void getAchievementDefinitionById_Success() {
        when(definitionRepository.findById(definitionId)).thenReturn(Optional.of(entity));

        AchievementDefinitionResponse response = definitionService.getAchievementDefinitionById(definitionId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(definitionId);
    }

    @Test
    void updateAchievementDefinition_Deactivate_Success() {
        UpdateAchievementDefinitionRequest request = new UpdateAchievementDefinitionRequest();
        request.setActive(false);

        when(definitionRepository.findById(definitionId)).thenReturn(Optional.of(entity));
        when(definitionRepository.save(any(AchievementDefinitionEntity.class))).thenAnswer(i -> i.getArgument(0));

        AchievementDefinitionResponse response = definitionService.updateAchievementDefinition(definitionId, request);

        assertThat(response).isNotNull();
        assertThat(response.getActive()).isFalse();
    }
}
