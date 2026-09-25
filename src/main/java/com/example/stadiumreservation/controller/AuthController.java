package com.example.stadiumreservation.controller;

import com.example.stadiumreservation.dto.AuthResponse;
import com.example.stadiumreservation.dto.LoginRequest;
import com.example.stadiumreservation.dto.RegisterRequest;
import com.example.stadiumreservation.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Yeni istifadəçi qeydiyyatı endpoint-i
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * İstifadəçi girişi (Login) endpoint-i
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
