package com.example.stadiumreservation.services;

import com.example.stadiumreservation.dto.PaymentRequest;
import com.example.stadiumreservation.dto.PaymentResponse;
import com.example.stadiumreservation.entity.Payment;
import com.example.stadiumreservation.entity.PaymentType;
import com.example.stadiumreservation.entity.Reservation;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.repository.PaymentRepository;
import com.example.stadiumreservation.repository.ReservationRepository;
import com.example.stadiumreservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    public PaymentService(PaymentRepository paymentRepository,
                          UserRepository userRepository,
                          ReservationRepository reservationRepository,
                          UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.userService = userService;
    }

    /**
     * Ödənişi həyata keçirən metod (Payment Process - Həm Rezervasiya, Həm Premium Abunəlik)
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        Reservation reservation = null;
        if (request.getReservationId() != null) {
            reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Rezervasiya tapılmadı!"));
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setReservation(reservation);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        // Defolt olaraq növü təyin edirik
        PaymentType type = request.getPaymentType() != null ? request.getPaymentType() : PaymentType.PREMIUM_SUBSCRIPTION;
        payment.setPaymentType(type);

        boolean isPremiumNow = Boolean.TRUE.equals(user.getIsPremium());

        // 💳 Kart nömrəsi yoxlaması simulyasiyası
        if ("CARD".equalsIgnoreCase(request.getPaymentMethod()) &&
                (request.getCardNumber() == null || request.getCardNumber().length() < 16)) {
            payment.setStatus("FAILED"); // Kart nömrəsi yanlışdırsa ödəniş uğursuz olur
            payment.setTransactionId("FAILED-" + UUID.randomUUID().toString().substring(0, 8));
        } else {
            payment.setStatus("SUCCESS"); // Ödəniş uğurla tamamlandı
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            // 1. Rezervasiya ödənilibsə statusunu "PAID" edirik
            if (reservation != null) {
                reservation.setStatus("PAID");
                reservationRepository.save(reservation);
            }

            // 2. Əgər ödəniş Premium Abunəlik üçündürsə, istifadəçini Premium edirik
            if (type == PaymentType.PREMIUM_SUBSCRIPTION) {
                int discount = request.getDiscountPercentage() != null ? request.getDiscountPercentage() : 15;
                int duration = request.getDurationInDays() != null ? request.getDurationInDays() : 30;

                userService.makeUserPremium(user.getId(), discount, duration);
                isPremiumNow = true;
            }
        }

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .userId(savedPayment.getUser().getId())
                .reservationId(savedPayment.getReservation() != null ? savedPayment.getReservation().getId() : null)
                .amount(savedPayment.getAmount())
                .paymentType(savedPayment.getPaymentType())
                .paymentMethod(savedPayment.getPaymentMethod())
                .status(savedPayment.getStatus())
                .transactionId(savedPayment.getTransactionId())
                .createdAt(savedPayment.getCreatedAt())
                .isPremiumNow(isPremiumNow)
                .build();
    }

    /**
     * İstifadəçinin ödəniş tarixçəsini gətirən metod
     */
    public List<PaymentResponse> getUserPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(p -> PaymentResponse.builder()
                        .paymentId(p.getId())
                        .userId(p.getUser().getId())
                        .reservationId(p.getReservation() != null ? p.getReservation().getId() : null)
                        .amount(p.getAmount())
                        .paymentType(p.getPaymentType())
                        .paymentMethod(p.getPaymentMethod())
                        .status(p.getStatus())
                        .transactionId(p.getTransactionId())
                        .createdAt(p.getCreatedAt())
                        .isPremiumNow(Boolean.TRUE.equals(p.getUser().getIsPremium()))
                        .build()
                )
                .collect(Collectors.toList());
    }
}