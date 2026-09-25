package com.example.stadiumreservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "field_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"field_id", "user_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "field_id", nullable = false)
    private Long fieldId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "rating", nullable = false)
    private Double rating;

    public FieldVote(Long fieldId, Long userId, Double rating) {
        this.fieldId = fieldId;
        this.userId = userId;
        this.rating = rating;
    }
}