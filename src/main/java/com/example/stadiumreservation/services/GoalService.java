package com.example.stadiumreservation.services;

import com.example.stadiumreservation.dto.GoalRequest;
import com.example.stadiumreservation.dto.GoalResponse;
import com.example.stadiumreservation.entity.Goal;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.repository.GoalRepository;
import com.example.stadiumreservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalVoteService goalVoteService; // ⭐ Ayrı servis bura daxil edildi

    public GoalService(GoalRepository goalRepository, UserRepository userRepository, GoalVoteService goalVoteService) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.goalVoteService = goalVoteService;
    }

    /**
     * Yeni qol videosunu sisteme elave edir
     */
    public GoalResponse addGoal(GoalRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setVideoUrl(request.getVideoUrl());
        goal.setDescription(request.getDescription());
        goal.setVotesCount(0);
        goal.setIsWeeklyWinner(false);

        Goal savedGoal = goalRepository.save(goal);
        return mapToResponse(savedGoal, request.getUserId());
    }

    /**
     * Bütün qolları səs sayına görə sıralayıb qaytarır
     */
    public List<GoalResponse> getAllGoals(Long userId) {
        return goalRepository.findAllByOrderByVotesCountDesc()
                .stream()
                .map(goal -> mapToResponse(goal, userId))
                .collect(Collectors.toList());
    }

    /**
     * Qola +1 səs əlavə edir (GoalVoteService vasitəsilə yoxlayaraq)
     */
    @Transactional
    public GoalResponse voteForGoal(Long goalId, Long userId) {
        // 1. GoalVoteService vasitəsilə səsverməni qeydə alırıq (artıq səs veribsə xəta atacaq)
        goalVoteService.recordVote(userId, goalId);

        // 2. Qolu tapıb səs sayını 1 vahid artırırıq
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Qol tapılmadı!"));

        goal.setVotesCount(goal.getVotesCount() + 1);
        Goal updatedGoal = goalRepository.save(goal);

        return mapToResponse(updatedGoal, userId);
    }

    /**
     * Ən çox səs toplayan qolu Həftənin Qolu elan edir
     */
    public GoalResponse selectWeeklyWinner() {
        List<Goal> goals = goalRepository.findAllByOrderByVotesCountDesc();
        if (goals.isEmpty()) {
            throw new RuntimeException("Siyahıda heç bir qol yoxdur!");
        }

        goalRepository.findByIsWeeklyWinnerTrue().ifPresent(previousWinner -> {
            previousWinner.setIsWeeklyWinner(false);
            goalRepository.save(previousWinner);
        });

        Goal winner = goals.get(0);
        winner.setIsWeeklyWinner(true);
        Goal savedWinner = goalRepository.save(winner);

        return mapToResponse(savedWinner, null);
    }

    /**
     * 🔒 Yalnız Premium istifadəçilərə video yükləmə icazəsi verir
     */
    public String downloadGoalVideo(Long goalId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        if (Boolean.FALSE.equals(user.getIsPremium()) ||
                user.getPremiumExpireDate() == null ||
                user.getPremiumExpireDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Bu videonu yükləmək üçün Premium abunəliyiniz olmalıdır!");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Qol tapılmadı!"));

        return goal.getVideoUrl();
    }

    // Mapper: GoalVoteService vasitəsilə user-in səs verib-vermədiyini öyrənir
    // Mapper: GoalVoteService vasitəsilə user-in səs verib-vermədiyini öyrənir
    private GoalResponse mapToResponse(Goal goal, Long currentUserId) {
        boolean hasVoted = goalVoteService.hasUserVoted(currentUserId, goal.getId());

        return new GoalResponse(
                goal.getId(),
                goal.getUser() != null ? goal.getUser().getFullName() : "Anonim",
                goal.getVideoUrl(),
                goal.getDescription(),
                goal.getVotesCount(),
                goal.getIsWeeklyWinner(),
                goal.getCreatedAt(),
                hasVoted // ⭐ 8-ci parametr kimi bura əlavə edirik
        );
    }
}