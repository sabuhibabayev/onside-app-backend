package com.example.stadiumreservation.controller;

import com.example.stadiumreservation.dto.CustomPage;
import com.example.stadiumreservation.dto.FieldRequest;
import com.example.stadiumreservation.entity.Field;
import com.example.stadiumreservation.services.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FieldController {

    private final FieldService fieldService;

    @GetMapping
    @Operation(summary = "Bütün meydançaları səhifələmə və çeşidləmə ilə gətir")
    public ResponseEntity<CustomPage<Field>> getAllFields(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir
    ) {
        var pageResult = fieldService.getAllFieldsPaged(page, size, sortBy, sortDir);
        return ResponseEntity.ok(CustomPage.of(pageResult));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<Field> createField(@Valid @RequestBody FieldRequest request, java.security.Principal principal) {
        String ownerEmail = principal.getName();
        return ResponseEntity.ok(fieldService.createField(request, ownerEmail));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Field>> getFieldsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(fieldService.getFieldsByOwner(ownerId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID-yə görə tək meydançanın detallarını gətir")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.getFieldById(id));
    }

    // ⭐ STADİONA SƏS VERMƏ ENDPOINT-İ (Yeni əlavə olundu)
    @PostMapping("/{fieldId}/vote")
    @Operation(summary = "Stadiona səs ver və reytinqi yenilə")
    public ResponseEntity<Field> voteField(
            @PathVariable Long fieldId,
            @RequestParam Long userId,
            @RequestParam Double rating
    ) {
        return ResponseEntity.ok(fieldService.voteField(fieldId, userId, rating));
    }
}