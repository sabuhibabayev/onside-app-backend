package com.example.stadiumreservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FieldRequest {

    @NotBlank(message = "Meydançanın adı boş ola bilməz!")
    private String name;

    @NotBlank(message = "Ünvan daxil edilməlidir!")
    private String address;

    @NotNull(message = "Saatlıq qiymət daxil edilməlidir!")
    @Positive(message = "Saatlıq qiymət sıfırdan böyük (müsbət) olmalıdır!")
    private Double pricePerHour;

    private String coverType;

    private Boolean hasLighting;

    private Boolean hasShower;
    // Meydançanın ölçüsü (məsələn: "5x5", "7x7")
    private String fieldSize;

    // Maksimum oyunçu sayı
    private Integer maxPlayers;
    private String imageUrl;

//    @NotNull(message = "Meydança sahibinin ID-si daxil edilməlidir!")
//    private Long ownerId;
}