package com.example.stadiumreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSearchResponse {

    private Long reservationId;
    private String fieldName;      // Meydançanın adı
    private String fieldAddress;   // Ünvanı
    private LocalDate date;        // Tarix
    private LocalTime startTime;   // Başlama saatı
    private LocalTime endTime;     // Bitmə saatı

    private String organizerName;  // Elanı paylaşanın adı
    private String organizerPhone; // Əlaqə nömrəsi

    private Integer neededPlayers; // Neçə nəfər lazımdır
    private String description;    // Açıqlama
}