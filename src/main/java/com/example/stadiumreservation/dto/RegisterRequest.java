package com.example.stadiumreservation.dto;

import com.example.stadiumreservation.entity.Role; // <-- Role Enum/Entity-ni import edin!
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Ad və soyad daxil edilməlidir!")
    private String fullName;

    @NotBlank(message = "Email daxil edilməlidir!")
    @Email(message = "Düzgün email formatı daxil edin!")
    private String email;

    @NotBlank(message = "Şifrə daxil edilməlidir!")
    @Size(min = 6, message = "Şifrə minimum 6 simvoldan ibarət olmalıdır!")
    private String password;

    private String phone;

    private Role role; // <-- String əvəzinə Role yazın!
}
