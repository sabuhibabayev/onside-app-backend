package com.example.stadiumreservation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"voter_id", "target_user_id"}) // Eyni istifadəçi eyni adama 2 dəfə səs verə bilməz!
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voter_id", nullable = false)
    private Long voterId; // Səs verən istifadəçinin ID-si

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId; // Səs verilən istifadəçinin ID-si

    @Column(nullable = false)
    private Double stars; // Verilən ulduz (1.0 - 5.0)
}