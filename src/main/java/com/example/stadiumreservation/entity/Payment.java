package com.example.stadiumreservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments") // Ödənişlər cədvəli
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Ödənişi edən istifadəçi

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation; // Ödənilən rezervasiya (əgər varsa)

    @Column(name = "amount", nullable = false)
    private Double amount; // Məbləğ (AZN)

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType; // Ödənişin növü (SUBSCRIPTION və ya RESERVATION)

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Məsələn: "CARD", "CASH"

    @Column(name = "status", nullable = false)
    private String status; // Məsələn: "SUCCESS", "FAILED", "PENDING"

    @Column(name = "transaction_id")
    private String transactionId; // Transaksiya kod/nömrəsi (Məs: TXN-984213)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Ödəniş vaxtı
}