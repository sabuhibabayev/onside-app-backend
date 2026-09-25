package com.example.stadiumreservation.services;

import com.example.stadiumreservation.dto.AuthResponse;
import com.example.stadiumreservation.dto.LoginRequest;
import com.example.stadiumreservation.dto.RegisterRequest;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.repository.UserRepository;
import com.example.stadiumreservation.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Yeni istifadəçini (Oyunçu və ya Meydança Sahibi) bazada qeydiyyatdan keçirir,
     * şifrəsini BCrypt ilə hash-ləyir və ona JWT Token qaytarır.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email artıq istifadə olunur!");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        userRepository.save(user);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());

        String token = jwtService.generateToken(extraClaims, user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    /**
     * İstifadəçinin email və şifrəsini doğrulayır,
     * məlumatlar düzgündürsə yeni JWT Token generasiya edir.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Şifrə yanlışdır!");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());

        String token = jwtService.generateToken(extraClaims, user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
