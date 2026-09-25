package com.example.stadiumreservation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "fields")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "hourly_price", nullable = false)
    private Double pricePerHour = 0.0;

    @Column(name = "cover_type")
    private String coverType;

    @Column(name = "has_lighting")
    private Boolean hasLighting = true;

    @Column(name = "has_shower")
    private Boolean hasShower = true;

    @Column(name = "field_size")
    private String fieldSize;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "max_players")
    private Integer maxPlayers;

    // ⭐ REYTİNQ SAHƏLƏRİ (Əlavə olundu)
    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "vote_count")
    private Integer voteCount = 0;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> reservations;
}