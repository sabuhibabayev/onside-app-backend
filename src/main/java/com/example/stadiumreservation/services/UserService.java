package com.example.stadiumreservation.services;

import com.example.stadiumreservation.dto.UserResponse;
import com.example.stadiumreservation.entity.Role;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.entity.UserRating;
import com.example.stadiumreservation.repository.UserRatingRepository;
import com.example.stadiumreservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }
        return userRepository.save(user);
    }

    @Autowired
    private UserRatingRepository userRatingRepository;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı ID: " + id));
    }

    /**
     * 🆕 Sizin Controller-in çağırdığı eyni parametrli metod:
     * @param userId - İstifadəçinin ID-si
     * @param discountPercentage - Tətbiq ediləcək endirim faizi (məsələn: 15)
     * @param durationInDays - Premium abunəlik müddəti (günlərlə, məsələn: 30)
     */
    public User makeUserPremium(Long userId, Integer discountPercentage, Integer durationInDays) {
        User user = getUserById(userId);

        user.setIsPremium(true);
        user.setDiscountPercentage(discountPercentage != null ? discountPercentage : 15);

        // Əgər istifadəçinin artıq aktiv premiumu varsa, yeni günləri onun üstünə gəlirik
        LocalDateTime baseDate = (user.getPremiumExpireDate() != null && user.getPremiumExpireDate().isAfter(LocalDateTime.now()))
                ? user.getPremiumExpireDate()
                : LocalDateTime.now();

        int daysToAdd = durationInDays != null ? durationInDays : 30;
        user.setPremiumExpireDate(baseDate.plusDays(daysToAdd));

        return userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı! E-poçt: " + email));
    }

    // İstifadəçiyə/Profilə ulduz səsverməsi (1 - 5 arası)
    public UserResponse rateUser(Long voterId, Long targetUserId, Double stars) {
        if (stars < 1 || stars > 5) {
            throw new RuntimeException("Səs xalı 1 ilə 5 arasında olmalıdır!");
        }

        if (voterId.equals(targetUserId)) {
            throw new RuntimeException("İstifadəçi öz profilinə səs verə bilməz!");
        }

        // 1. Zəmanət yoxlaması: Daha öncə səs veribmi?
        if (userRatingRepository.existsByVoterIdAndTargetUserId(voterId, targetUserId)) {
            throw new RuntimeException("Siz bu profilə artıq səs vermisiniz!");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        // 2. Səsi bazada qeydə alırıq
        userRatingRepository.save(UserRating.builder()
                .voterId(voterId)
                .targetUserId(targetUserId)
                .stars(stars)
                .build());

        // 3. Yeni reytinqi dəqiq hesblayırıq
        double newTotalStars = (targetUser.getTotalStars() != null ? targetUser.getTotalStars() : 0) + stars;
        int newCount = (targetUser.getRatingCount() != null ? targetUser.getRatingCount() : 0) + 1;
        double newRating = Math.round((newTotalStars / newCount) * 10.0) / 10.0;

        targetUser.setTotalStars(newTotalStars);
        targetUser.setRatingCount(newCount);
        targetUser.setRating(newRating);

        User savedUser = userRepository.save(targetUser);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getIsPremium(),
                savedUser.getDiscountPercentage(),
                savedUser.getPremiumExpireDate(),
                savedUser.getRating(),
                savedUser.getRatingCount()
        );
    }

    public boolean checkPremiumStatus(Long userId) {
        User user = getUserById(userId);

        if (Boolean.TRUE.equals(user.getIsPremium()) && user.getPremiumExpireDate() != null) {
            if (user.getPremiumExpireDate().isBefore(LocalDateTime.now())) {
                user.setIsPremium(false);
                user.setDiscountPercentage(0);
                userRepository.save(user);
                return false;
            }
            return true;
        }
        return false;
    }
}
