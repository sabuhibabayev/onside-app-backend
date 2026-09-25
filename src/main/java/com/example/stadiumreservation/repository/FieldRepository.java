package com.example.stadiumreservation.repository;

import com.example.stadiumreservation.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FieldRepository extends JpaRepository<Field, Long> {

    List<Field> findByOwnerId(Long ownerId); // Köhnə metod (xəta çıxmasın deyə qalsın)

    Page<Field> findByOwnerId(Long ownerId, Pageable pageable); // Yeni səhifələmə metodu
}