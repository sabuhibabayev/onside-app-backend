package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.GoalVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GoalVoteRepository extends JpaRepository<GoalVote, Long> {
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);
}