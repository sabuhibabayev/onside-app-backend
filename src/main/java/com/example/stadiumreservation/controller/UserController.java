package com.example.stadiumreservation.controller;

import com.example.stadiumreservation.dto.UserResponse;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/users/{userId}/upgrade-to-premium
     * İstifadəçiyə Premium paket təyin edən endpoint
     */
    @PostMapping("/{userId}/upgrade-to-premium")
    public ResponseEntity<User> makePremium(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "15") Integer discountPercentage,
            @RequestParam(defaultValue = "30") Integer durationInDays) {

        return ResponseEntity.ok(userService.makeUserPremium(userId, discountPercentage, durationInDays));
    }

    // Daxil olan istifadəçinin profil məlumatlarını token vasitəsilə gətirmək üçün
    @GetMapping("/me")
    @Operation(summary = "Token vasitəsilə cari daxil olmuş istifadəçi məlumatlarını gətir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        String email = principal.getName();
        // UserService daxilində və ya birbaşa UserRepository ilə istifadəçini tapa bilərsən
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/rate")
    @Operation(summary = "İstifadəçiyə səs ver (1-5 ulduz)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> rateUser(
            @PathVariable Long userId,
            @RequestParam Double stars,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.rateUser(currentUser.getId(), userId, stars));
    }

}