package com.example.stadiumreservation.controller;

import com.example.stadiumreservation.dto.GoalRequest;
import com.example.stadiumreservation.dto.GoalResponse;
import com.example.stadiumreservation.services.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // Yeni video yükləmək üçün
    @PostMapping
    public ResponseEntity<GoalResponse> addGoal(@RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.addGoal(request));
    }

    // Bütün videoları səs sırasına görə gətirmək üçün (userId parametr olaraq göndərilə bilər)
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(goalService.getAllGoals(userId));
    }

    // Videoya səs vermək üçün (userId göndərilir)
    @PostMapping("/{id}/vote")
    public ResponseEntity<GoalResponse> voteForGoal(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(goalService.voteForGoal(id, userId));
    }

    // Həftənin qolunu seçmək üçün
    @PostMapping("/select-weekly-winner")
    public ResponseEntity<GoalResponse> selectWeeklyWinner() {
        return ResponseEntity.ok(goalService.selectWeeklyWinner());
    }

    // 📥 Premium istifadəçi üçün video yükləmək endpoint-i
    @GetMapping("/{goalId}/download")
    public ResponseEntity<String> downloadVideo(@PathVariable Long goalId, @RequestParam Long userId) {
        return ResponseEntity.ok(goalService.downloadGoalVideo(goalId, userId));
    }
}