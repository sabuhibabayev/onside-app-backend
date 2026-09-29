package com.example.stadiumreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalResponse {
    private Long id;
    private String userName;
    private String videoUrl;
    private String description;
    private Integer votesCount;
    private Boolean isWeeklyWinner;
    private LocalDateTime createdAt;
    private boolean hasVoted; // ⭐ Səs verilib-verilmədiyini saxlayan sahə
}