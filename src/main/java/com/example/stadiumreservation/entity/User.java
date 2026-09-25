package com.example.stadiumreservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    private Boolean isPremium = false;
    private Integer discountPercentage = 0;

    private LocalDateTime premiumExpireDate;

    // --- UserDetails İnterfeys Metodları ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Rolunuzu Spring Security-nin tanıdığı formada qaytarırıq
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // DİQQƏT: Bura email qaytarmalıdır! Boş "" ola bilməz.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Gerekli Getter / Setter-lər (Lombok @Data olduğu üçün çoxuna ehtiyac yoxdur, amma saxlamaq olar)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // Reytinq üçün əlavə olunan sahələr
    @Builder.Default
    private Double rating = 5.0; // Orta göstərici (məs: 4.9)

    @Builder.Default
    private Integer ratingCount = 1; // Səs verənlərin sayı

    @Builder.Default
    private Double totalStars = 5.0; // Ümumi toplanan xal

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getIsPremium() { return isPremium; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public LocalDateTime getPremiumExpireDate() { return premiumExpireDate; }
    public void setPremiumExpireDate(LocalDateTime premiumExpireDate) { this.premiumExpireDate = premiumExpireDate; }
}