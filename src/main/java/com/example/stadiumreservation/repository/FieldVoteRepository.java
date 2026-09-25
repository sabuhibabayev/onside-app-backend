package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.FieldVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldVoteRepository extends JpaRepository<FieldVote, Long> {
    boolean existsByFieldIdAndUserId(Long fieldId, Long userId);
}