package com.example.stadiumreservation.services;

import com.example.stadiumreservation.entity.GoalVote;
import com.example.stadiumreservation.repository.GoalVoteRepository;
import org.springframework.stereotype.Service;

@Service
public class GoalVoteService {

    private final GoalVoteRepository goalVoteRepository;

    public GoalVoteService(GoalVoteRepository goalVoteRepository) {
        this.goalVoteRepository = goalVoteRepository;
    }

    /**
     * İstifadəçinin verilmiş videoya səs verib-vermədiyini yoxlayır
     */
    public boolean hasUserVoted(Long userId, Long videoId) {
        if (userId == null || videoId == null) {
            return false;
        }
        return goalVoteRepository.existsByUserIdAndVideoId(userId, videoId);
    }

    /**
     * İstifadəçinin səsini bazaya qeyd edir (Əgər artıq veribsə xəta atır)
     */
    public void recordVote(Long userId, Long videoId) {
        if (userId == null) {
            throw new RuntimeException("Səs vermək üçün istifadəçi ID-si vacibdir!");
        }

        if (hasUserVoted(userId, videoId)) {
            throw new RuntimeException("Siz bu videoya artıq səs vermisiniz!");
        }

        GoalVote vote = GoalVote.builder()
                .userId(userId)
                .videoId(videoId)
                .build();

        goalVoteRepository.save(vote);
    }
}