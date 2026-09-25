package com.example.stadiumreservation.dto;

import com.example.stadiumreservation.entity.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long userId;
    private Long reservationId;
    private Double amount;
    private PaymentType paymentType;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
    private Boolean isPremiumNow; // Əgər abunəlik ödənişidirsə, istifadəçinin indikı statusunu qaytarır
}