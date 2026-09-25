package com.example.stadiumreservation.dto;

import com.example.stadiumreservation.entity.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long userId;
    private Long reservationId;
    private Double amount;
    private PaymentType paymentType; // PREMIUM_SUBSCRIPTION və ya RESERVATION_DEPOSIT
    private String paymentMethod; // "CARD" və ya "CASH"
    private String cardNumber;    // Simulyasiya üçün kart nömrəsi (Məs: 4169....)
    private Integer discountPercentage; // Premium abunəlik üçün endirim faizi (Məs: 15)
    private Integer durationInDays;     // Premium abunəlik müddəti (Məs: 30)
}