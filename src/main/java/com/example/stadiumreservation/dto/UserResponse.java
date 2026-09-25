package com.example.stadiumreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    // İstifadəçinin xaricə (Swagger/Frontend) göstəriləcək təmiz məlumat modeli
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Boolean isPremium;
    private Integer discountPercentage;
    private LocalDateTime premiumExpireDate;
    // Reytinq məlumatları
    private Double rating;
    private Integer ratingCount;
}
