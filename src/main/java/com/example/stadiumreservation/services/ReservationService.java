package com.example.stadiumreservation.services;

import com.example.stadiumreservation.dto.PlayerSearchResponse;
import com.example.stadiumreservation.dto.ReservationRequest;
import com.example.stadiumreservation.dto.ReservationResponse;
import com.example.stadiumreservation.entity.Field;
import com.example.stadiumreservation.entity.Reservation;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.repository.FieldRepository;
import com.example.stadiumreservation.repository.ReservationRepository;
import com.example.stadiumreservation.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              FieldRepository fieldRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
    }

    /**
     * Yeni rezervasiya yaradır (Status avtomatik PENDING olur)
     */
    public ReservationResponse createReservation(ReservationRequest request) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 1. Keçmiş tarix yoxlaması
        if (request.getReservationDate().isBefore(today)) {
            throw new IllegalArgumentException("Keçmiş tarix üçün rezervasiya etmək olmaz!");
        }

        // 2. Bugünkü gün üçün keçmiş saat yoxlaması
        if (request.getReservationDate().isEqual(today) && request.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException("Keçmiş saat üçün rezervasiya etmək olmaz!");
        }

        // 3. Çakışma yoxlanılır
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                request.getFieldId(),
                request.getReservationDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Bu meydança seçilmiş saat aralığında artıq rezerv olunub!");
        }

        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        Field field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("Meydança tapılmadı!"));

        double originalPrice = field.getPricePerHour();
        double finalPrice = originalPrice;

        boolean isPremiumActive = Boolean.TRUE.equals(user.getIsPremium())
                && user.getPremiumExpireDate() != null
                && user.getPremiumExpireDate().isAfter(java.time.LocalDateTime.now());

        if (isPremiumActive && user.getDiscountPercentage() != null && user.getDiscountPercentage() > 0) {
            double discount = (originalPrice * user.getDiscountPercentage()) / 100.0;
            finalPrice = originalPrice - discount;
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setField(field);
        reservation.setReservationDate(request.getReservationDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus("PENDING");
        reservation.setTotalPrice(finalPrice);

        if (request.getIsLookingForPlayers() != null) {
            reservation.setIsLookingForPlayers(request.getIsLookingForPlayers());
        }
        reservation.setNeededPlayers(request.getNeededPlayers() != null ? request.getNeededPlayers() : 0);
        reservation.setDescription(request.getDescription());

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    // Entity -> Response DTO Mapper
    private ReservationResponse mapToResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .fieldId(r.getField() != null ? r.getField().getId() : null)
                .fieldName(r.getField() != null ? r.getField().getName() : null)
                .fieldAddress(r.getField() != null ? r.getField().getAddress() : null)
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .userFullName(r.getUser() != null ? r.getUser().getFullName() : null)
                .userPhone(r.getUser() != null ? r.getUser().getPhone() : null)
                .reservationDate(r.getReservationDate())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .status(r.getStatus())
                .totalPrice(r.getTotalPrice())
                .isLookingForPlayers(r.getIsLookingForPlayers())
                .neededPlayers(r.getNeededPlayers())
                .description(r.getDescription())
                .build();
    }

    // PENDING rezervasiyaları DTO siyahısı kimi qaytarır
    public List<ReservationResponse> getPendingReservationsByFieldId(Long fieldId) {
        return reservationRepository.findByFieldIdAndStatus(fieldId, "PENDING")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔑 ADDED FOR ADMIN PANEL: Admin ✓ düyməsinə basdıqda statusu APPROVED edir
    public ReservationResponse approveReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rezervasiya tapılmadı! ID: " + id));

        reservation.setStatus("APPROVED");
        Reservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    public List<ReservationResponse> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReservationResponse> getMyReservations() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        return reservationRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void cancelReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Rezervasiya tapılmadı! ID: " + id);
        }
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> getActiveReservationsByFieldId(Long fieldId) {
        return reservationRepository.findByFieldIdAndStatusNot(fieldId, "CANCELLED")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }



    public List<PlayerSearchResponse> getReservationsLookingForPlayers() {
        List<Reservation> reservations = reservationRepository.findByIsLookingForPlayersTrue();

        return reservations.stream().map(r -> new PlayerSearchResponse(
                r.getId(),
                r.getField().getName(),
                r.getField().getAddress(),
                r.getReservationDate(),
                r.getStartTime(),
                r.getEndTime(),
                r.getUser().getFullName(),
                r.getUser().getPhone(),
                r.getNeededPlayers(),
                r.getDescription()
        )).collect(Collectors.toList());
    }
}