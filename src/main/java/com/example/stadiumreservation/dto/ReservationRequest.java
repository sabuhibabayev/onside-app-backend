package com.example.stadiumreservation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservationRequest {

//    @NotNull(message = "İstifadəçi ID-si boş ola bilməz!")
//    private Long userId;

    @NotNull(message = "Meydança ID-si boş ola bilməz!")
    private Long fieldId;

    @NotNull(message = "Rezervasiya tarixi seçilməlidir!")
    @FutureOrPresent(message = "Rezervasiya tarixi keçmiş tarix ola bilməz!")
    private LocalDate reservationDate;

    @NotNull(message = "Başlama saatı seçilməlidir!")
    private LocalTime startTime;

    @NotNull(message = "Bitmə saatı seçilməlidir!")
    private LocalTime endTime;

    private Boolean isLookingForPlayers;

    @PositiveOrZero(message = "Axtarılan oyunçu sayı mənfi ola bilməz!")
    private Integer neededPlayers;

    private String description;
}
