package com.example.stadiumreservation.controller;

import com.example.stadiumreservation.dto.PlayerSearchResponse;
import com.example.stadiumreservation.dto.ReservationRequest;
import com.example.stadiumreservation.dto.ReservationResponse;
import com.example.stadiumreservation.entity.Reservation;
import com.example.stadiumreservation.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Controller", description = "Meydança rezervasiyası və oyunçu axtarışı əməliyyatları")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Yeni rezervasiya yarat", description = "İstifadəçi seçilmiş meydança üçün tarix və saat aralığında bron yaradır.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @GetMapping("/looking-for-players")
    @Operation(summary = "Oyunçu axtaran rezervasiyaları gətir", description = "Komandasına və ya oyununa əlavə oyunçu axtaran bütün aktiv rezervasiyaları siyahılayır.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlayerSearchResponse>> getReservationsLookingForPlayers() {
        return ResponseEntity.ok(reservationService.getReservationsLookingForPlayers());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "İstifadəçinin rezervasiya tarixçəsini gətir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponse>> getUserReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }

    // 🔑 ADDED FOR ADMIN PANEL: Gözləmədə olan müraciətlər
    @GetMapping("/field/{fieldId}/pending")
    @Operation(summary = "Meydançaya aid gözləmədə olan rezervasiyaları gətir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponse>> getPendingReservationsByFieldId(@PathVariable Long fieldId) {
        return ResponseEntity.ok(reservationService.getPendingReservationsByFieldId(fieldId));
    }

    // 🔑 ADDED FOR ADMIN PANEL: Rezervasiyanı təsdiqləmək (✓ düyməsi üçün)
    @PutMapping("/{id}/approve")
    @Operation(summary = "Rezervasiyanı təsdiqlə (APPROVED et)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponse> approveReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.approveReservation(id));
    }
    @GetMapping("/field/{fieldId}/active")
    @Operation(summary = "Meydançaya aid aktiv (dolu) rezervasiyaları gətir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponse>> getActiveReservationsByFieldId(@PathVariable Long fieldId) {
        return ResponseEntity.ok(reservationService.getActiveReservationsByFieldId(fieldId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Rezervasiyanı ləğv et (sil)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-reservations")
    @Operation(summary = "Daxil olmuş istifadəçinin öz rezervasiya tarixçəsini gətir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        return ResponseEntity.ok(reservationService.getMyReservations());
    }
}