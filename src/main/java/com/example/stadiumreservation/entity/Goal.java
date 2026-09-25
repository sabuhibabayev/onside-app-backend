package com.example.stadiumreservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Qolu paylaşan istifadəçi

    @Column(nullable = false)
    private String videoUrl; // Youtube və ya video linki

    private String description; // Qol haqqında qısa söz (Məs: 25 metrdən 90-lığa)

    private Integer votesCount = 0; // Səs sayı (default: 0)

    private Boolean isWeeklyWinner = false; // Həftənin qolu seçilibmi?

    private LocalDateTime createdAt = LocalDateTime.now();
}
