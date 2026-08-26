package com.rathinam.toastmasters.modules.ranking.service;

import com.rathinam.toastmasters.modules.achievement.service.AchievementEvaluationService;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@Transactional
public class ChampionshipService {

    private final RankingService rankingService;
    private final AchievementEvaluationService achievementEvaluationService;

    public ChampionshipService(RankingService rankingService,
                               AchievementEvaluationService achievementEvaluationService) {
        this.rankingService = rankingService;
        this.achievementEvaluationService = achievementEvaluationService;
    }

    public MonthlyRankingResponse getMonthlyChampionship(int year, int month) {
        MonthlyRankingResponse response = rankingService.getMonthlyRanking(year, month, PageRequest.of(0, 20));
        if (response.getChampion() != null) {
            achievementEvaluationService.awardMonthlyChampionAchievement(
                response.getChampion().getMemberId(),
                "Monthly Champion for " + year + "-" + String.format("%02d", month)
            );
        }
        return response;
    }

    public MonthlyRankingResponse getCurrentMonthlyChampionship() {
        YearMonth current = YearMonth.now();
        return getMonthlyChampionship(current.getYear(), current.getMonthValue());
    }
}
