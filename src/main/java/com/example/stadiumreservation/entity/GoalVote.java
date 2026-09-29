package com.example.stadiumreservation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "goal_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "video_id"}) // Eyni istifadəçi eyni videoya 2-ci dəfə səs verə bilməz
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;
}