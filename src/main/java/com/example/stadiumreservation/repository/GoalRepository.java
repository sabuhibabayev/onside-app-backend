package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    // Səs sayına görə çoxdan aza sıralanmış siyahı
    List<Goal> findAllByOrderByVotesCountDesc();

    // Həftənin qalibi seçilmiş qolu tapmaq üçün
    Optional<Goal> findByIsWeeklyWinnerTrue();
}
