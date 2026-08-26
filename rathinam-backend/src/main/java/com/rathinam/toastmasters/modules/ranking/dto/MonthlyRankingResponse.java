package com.rathinam.toastmasters.modules.ranking.dto;

import org.springframework.data.domain.Page;

public class MonthlyRankingResponse {
    private Integer year;
    private Integer month;
    private Long totalMembers;
    private Page<RankingEntryResponse> leaderboard;
    private MonthlyChampionResponse champion;

    public MonthlyRankingResponse() {
    }

    public MonthlyRankingResponse(Integer year, Integer month, Long totalMembers, Page<RankingEntryResponse> leaderboard, MonthlyChampionResponse champion) {
        this.year = year;
        this.month = month;
        this.totalMembers = totalMembers;
        this.leaderboard = leaderboard;
        this.champion = champion;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public Page<RankingEntryResponse> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(Page<RankingEntryResponse> leaderboard) {
        this.leaderboard = leaderboard;
    }

    public MonthlyChampionResponse getChampion() {
        return champion;
    }

    public void setChampion(MonthlyChampionResponse champion) {
        this.champion = champion;
    }
}
