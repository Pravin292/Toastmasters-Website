package com.rathinam.toastmasters.modules.ranking;

import com.rathinam.toastmasters.modules.ranking.dto.MonthlyChampionResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.service.ChampionshipService;
import com.rathinam.toastmasters.modules.ranking.service.RankingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;

@ExtendWith(MockitoExtension.class)
class ChampionshipServiceTest {

    @Mock
    private RankingService rankingService;

    @Mock
    private AchievementEvaluationService achievementEvaluationService;

    @InjectMocks
    private ChampionshipService championshipService;

    @Test
    void getMonthlyChampionship_Success() {
        MonthlyChampionResponse champion = new MonthlyChampionResponse(2026, 8, UUID.randomUUID(), "Pravin", "pravin@test.com", 148L);
        MonthlyRankingResponse response = new MonthlyRankingResponse(2026, 8, 1L, new PageImpl<>(List.of()), champion);

        when(rankingService.getMonthlyRanking(eq(2026), eq(8), any())).thenReturn(response);

        MonthlyRankingResponse result = championshipService.getMonthlyChampionship(2026, 8);

        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(2026);
        assertThat(result.getChampion()).isNotNull();
        assertThat(result.getChampion().getDisplayName()).isEqualTo("Pravin");
    }

    @Test
    void getCurrentMonthlyChampionship_Success() {
        YearMonth now = YearMonth.now();
        MonthlyRankingResponse response = new MonthlyRankingResponse(now.getYear(), now.getMonthValue(), 0L, new PageImpl<>(List.of()), null);

        when(rankingService.getMonthlyRanking(eq(now.getYear()), eq(now.getMonthValue()), any())).thenReturn(response);

        MonthlyRankingResponse result = championshipService.getCurrentMonthlyChampionship();

        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(now.getYear());
        assertThat(result.getMonth()).isEqualTo(now.getMonthValue());
    }
}
