package com.rathinam.toastmasters.modules.ranking.dto;

import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public class LeaderboardResponse {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Page<RankingEntryResponse> entries;

    public LeaderboardResponse() {
    }

    public LeaderboardResponse(LocalDateTime startDate, LocalDateTime endDate, Page<RankingEntryResponse> entries) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Page<RankingEntryResponse> getEntries() {
        return entries;
    }

    public void setEntries(Page<RankingEntryResponse> entries) {
        this.entries = entries;
    }
}
