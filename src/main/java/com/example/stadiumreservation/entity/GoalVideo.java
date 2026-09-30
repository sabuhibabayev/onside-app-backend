package com.example.stadiumreservation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal_videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // Qolun başlığı (məs: "Əli - Cərimə zərbəsi")
    @Column(columnDefinition = "TEXT")
    private String videoUrl;       // Buluddakı video linki (.mp4)
    private String ownerName;       // Stadionun və ya Owner-in adı
    private Long fieldId;           // Stadionun ID-si

    @Builder.Default
    private Integer voteCount = 0;  // Səs sayısı (defolt olaraq 0)

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}