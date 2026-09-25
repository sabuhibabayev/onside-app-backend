package com.example.stadiumreservation.services;

import com.example.stadiumreservation.dto.FieldRequest;
import com.example.stadiumreservation.entity.Field;
import com.example.stadiumreservation.entity.FieldVote;
import com.example.stadiumreservation.entity.User;
import com.example.stadiumreservation.repository.FieldRepository;
import com.example.stadiumreservation.repository.FieldVoteRepository;
import com.example.stadiumreservation.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FieldService {

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final FieldVoteRepository fieldVoteRepository; // 🆕 İnject olundu

    public FieldService(FieldRepository fieldRepository,
                        UserRepository userRepository,
                        FieldVoteRepository fieldVoteRepository) {
        this.fieldRepository = fieldRepository;
        this.userRepository = userRepository;
        this.fieldVoteRepository = fieldVoteRepository;
    }

    public Field createField(FieldRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Sahibkar (Owner) tapılmadı: " + ownerEmail));

        Field field = new Field();
        field.setName(request.getName());
        field.setAddress(request.getAddress());

        Double price = request.getPricePerHour() != null ? request.getPricePerHour() : 0.0;
        field.setPricePerHour(price);

        field.setCoverType(request.getCoverType() != null ? request.getCoverType() : "ARTIFICIAL");
        field.setHasLighting(request.getHasLighting() != null ? request.getHasLighting() : true);
        field.setHasShower(request.getHasShower() != null ? request.getHasShower() : true);

        field.setFieldSize(request.getFieldSize());
        field.setMaxPlayers(request.getMaxPlayers());

        field.setOwner(owner);

        return fieldRepository.save(field);
    }

    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }

    public List<Field> getFieldsByOwner(Long ownerId) {
        return fieldRepository.findByOwnerId(ownerId);
    }

    public Page<Field> getAllFieldsPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return fieldRepository.findAll(pageable);
    }

    public Field getFieldById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meydança tapılmadı!"));
    }

    // 🆕 YENİ: Stadiona səs vermə və reytinq hesablama metodu
    @Transactional
    public Field voteField(Long fieldId, Long userId, Double newRating) {
        // 1. İstifadəçinin bu stadiona daha əvvəl səs verib-vermədiyini yoxlayırıq
        if (fieldVoteRepository.existsByFieldIdAndUserId(fieldId, userId)) {
            throw new IllegalArgumentException("Siz bu stadiona artıq səs vermisiniz!");
        }

        // 2. Stadionu tapırıq
        Field field = getFieldById(fieldId);

        double currentRating = field.getRating() != null ? field.getRating() : 0.0;
        int currentVotes = field.getVoteCount() != null ? field.getVoteCount() : 0;

        // 3. Yeni ortalama reytinqin hesablanması
        double updatedRating = ((currentRating * currentVotes) + newRating) / (currentVotes + 1);

        // Vergüldən sonra 1 rəqəm saxlayırıq (məs: 4.8)
        field.setRating(Math.round(updatedRating * 10.0) / 10.0);
        field.setVoteCount(currentVotes + 1);

        // 4. Təkrar səs verilməməsi üçün səs tarixçəsini saxlayırıq
        fieldVoteRepository.save(new FieldVote(fieldId, userId, newRating));

        return fieldRepository.save(field);
    }
}