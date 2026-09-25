package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Bu metodu əlavə edin və ya mövcud metodu bununla əvəz edin:
    Optional<User> findByEmail(String email);

}
