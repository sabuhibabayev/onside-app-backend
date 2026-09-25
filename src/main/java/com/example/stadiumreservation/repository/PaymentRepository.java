package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Müəyyən bir istifadəçinin bütün ödəniş tarixçəsini gətirmək üçün
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
}