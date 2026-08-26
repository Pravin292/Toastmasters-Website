package com.rathinam.toastmasters.modules.ranking.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.modules.ranking.dto.MonthlyRankingResponse;
import com.rathinam.toastmasters.modules.ranking.service.ChampionshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/championships")
public class ChampionshipController {

    private final ChampionshipService championshipService;

    public ChampionshipController(ChampionshipService championshipService) {
        this.championshipService = championshipService;
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<ApiResponse<MonthlyRankingResponse>> getMonthlyChampionship(
            @PathVariable int year,
            @PathVariable int month) {
        MonthlyRankingResponse response = championshipService.getMonthlyChampionship(year, month);
        return ResponseEntity.ok(ApiResponse.success(response, "Monthly championship retrieved successfully"));
    }

    @GetMapping("/monthly/current")
    public ResponseEntity<ApiResponse<MonthlyRankingResponse>> getCurrentMonthlyChampionship() {
        MonthlyRankingResponse response = championshipService.getCurrentMonthlyChampionship();
        return ResponseEntity.ok(ApiResponse.success(response, "Current monthly championship retrieved successfully"));
    }
}
