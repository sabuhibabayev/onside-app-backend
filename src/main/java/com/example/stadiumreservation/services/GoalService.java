package com.example.stadiumreservation.services;

import com.example.stadiumreservation.dto.GoalRequest;
import com.example.stadiumreservation.dto.GoalResponse;
import com.example.stadiumreservation.entity.Goal;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.repository.GoalRepository;
import com.example.stadiumreservation.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
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
        goal.setVotesCount(0); // İlkin səs sayı
        goal.setIsWeeklyWinner(false); // İlkin olaraq qalib deyil

        Goal savedGoal = goalRepository.save(goal);
        return mapToResponse(savedGoal);
    }

    /**
     * Bütün qolları səs sayına görə sıralayıb qaytarır
     */
    public List<GoalResponse> getAllGoals() {
        return goalRepository.findAllByOrderByVotesCountDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Qola +1 səs əlavə edir
     */
    public GoalResponse voteForGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Qol tapılmadı!"));

        goal.setVotesCount(goal.getVotesCount() + 1); // Səsləri 1 vahid artırırıq
        Goal updatedGoal = goalRepository.save(goal);
        return mapToResponse(updatedGoal);
    }

    /**
     * Ən çox səs toplayan qolu Həftənin Qolu elan edir
     */
    public GoalResponse selectWeeklyWinner() {
        List<Goal> goals = goalRepository.findAllByOrderByVotesCountDesc();
        if (goals.isEmpty()) {
            throw new RuntimeException("Siyahıda heç bir qol yoxdur!");
        }

        // Əvvəlki qalib varsa statusunu sıfırlayırıq
        goalRepository.findByIsWeeklyWinnerTrue().ifPresent(previousWinner -> {
            previousWinner.setIsWeeklyWinner(false);
            goalRepository.save(previousWinner);
        });

        // 1-ci sıradakı (ən çox səs alan) qolu qalib edirik
        Goal winner = goals.get(0);
        winner.setIsWeeklyWinner(true);
        Goal savedWinner = goalRepository.save(winner);

        return mapToResponse(savedWinner);
    }

    /**
     * 🔒 Yalnız Premium istifadəçilərə video yükləmə icazəsi verir
     */
    public String downloadGoalVideo(Long goalId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        // İstifadəçinin Premium statusunu və müddətini yoxlayırıq
        if (Boolean.FALSE.equals(user.getIsPremium()) ||
                user.getPremiumExpireDate() == null ||
                user.getPremiumExpireDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Bu videonu yükləmək üçün Premium abunəliyiniz olmalıdır!");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Qol tapılmadı!"));

        return goal.getVideoUrl(); // Premiumdursa video keçidini qaytarır
    }

    // Əkiz kodları aradan qaldırmaq üçün yardımçı mapping metodu
    private GoalResponse mapToResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getUser().getFullName(),
                goal.getVideoUrl(),
                goal.getDescription(),
                goal.getVotesCount(),
                goal.getIsWeeklyWinner(),
                goal.getCreatedAt()
        );
    }
}