package com.example.stadiumreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private Long fieldId;
    private String fieldName;
    private String fieldAddress;

    private Double totalPrice;

    private Long userId;
    private String userFullName;
    private String userPhone;

    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String status;
    private Boolean isLookingForPlayers;
    private Integer neededPlayers;
    private String description;
}