package com.example.stadiumreservation.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"reservations", "fields", "hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    @JsonIgnoreProperties({"reservations", "owner", "hibernateLazyInitializer", "handler"})
    private Field field;

    @Column(nullable = false)
    private LocalDate reservationDate; // YYYY-MM-DD

    @Column(nullable = false)
    private LocalTime startTime; // 19:00

    @Column(nullable = false)
    private LocalTime endTime; // 20:00

    // İlkin dəyər olaraq PENDING təyin edilir
    @Column(nullable = false)
    private String status = "PENDING"; // "PENDING", "APPROVED", "CANCELLED"

    @Column(name = "is_looking_for_players")
    private Boolean isLookingForPlayers = false;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "needed_players")
    private Integer neededPlayers;

    @Column(name = "description")
    private String description;
}