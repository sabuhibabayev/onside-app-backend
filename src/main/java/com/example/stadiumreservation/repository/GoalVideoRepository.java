package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.GoalVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalVideoRepository extends JpaRepository<GoalVideo, Long> {

    // Top 10 videonu səs sayına görə sıralayıb gətirmək üçün
    List<GoalVideo> findTop10ByOrderByVoteCountDesc();
}