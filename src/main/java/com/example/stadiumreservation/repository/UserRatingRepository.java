package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    // Səs verənin daha öncə səs verib-vermədiyini yoxlayır
    boolean existsByVoterIdAndTargetUserId(Long voterId, Long targetUserId);
}