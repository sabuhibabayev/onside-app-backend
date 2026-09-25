package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.dto.ReservationResponse;
import com.example.stadiumreservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Eyni meydança üçün seçilmiş tarix və saat aralığında başqa rezervasiya varımmı yoxlayır
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.field.id = :fieldId " +
            "AND r.reservationDate = :date " +
            "AND r.status != 'CANCELLED' " +
            "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
    boolean existsOverlappingReservation(@Param("fieldId") Long fieldId,
                                         @Param("date") LocalDate date,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("endTime") LocalTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.field.id = :fieldId " +
            "AND r.reservationDate = :date " +
            "AND r.status != 'CANCELLED' " +
            "AND :startTime < r.endTime AND :endTime > r.startTime")
    List<Reservation> findConflictingReservations(
            @Param("fieldId") Long fieldId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // İstifadəçinin ID-sinə görə rezervasiyalarını tapmaq üçün
    List<Reservation> findByUserId(Long userId);

    // Meydançaya aid LƏĞV EDİLMƏMİŞ bütün (PENDING və APPROVED) rezervasiyaları gətirir
    List<Reservation> findByFieldIdAndStatusNot(Long fieldId, String status);

    // Oyunçu axtaranları gətirmək üçün
    List<Reservation> findByIsLookingForPlayersTrue();

    // 🔑 ADMIN PANEL ÜÇÜN ƏLAVƏ OLUNDU: Stadion ID və statusa görə axtarış (Məs: PENDING olanlar)
    List<Reservation> findByFieldIdAndStatus(Long fieldId, String status);

//    // ReservationService interface və ya class daxilində:
//    List<ReservationResponse> getMyReservations();
}
