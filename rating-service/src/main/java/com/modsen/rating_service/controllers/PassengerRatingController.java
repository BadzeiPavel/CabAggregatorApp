package com.modsen.rating_service.controllers;

import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.services.PassengerRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings/passengers")
@RequiredArgsConstructor
public class PassengerRatingController {

    private final PassengerRatingService service;

    @PostMapping
    public ResponseEntity<RatingDTO> createPassengerRating(@Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRatingDTO = service.createPassengerRating(ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRatingDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getPassengerRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.getPassengerRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{passengerId}/all")
    public ResponseEntity<GetAllPaginatedResponse<RatingDTO>> getPassengerRatings(
            @PathVariable String passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<RatingDTO> responseDTO =
                service.getPaginatedPassengerRatingsByPassengerId(passengerId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{passengerId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getPassengerRatingStatistic(@PathVariable String passengerId) {
        return ResponseEntity.ok(service.getAverageRating(passengerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updatePassengerRating(
            @PathVariable String id,
            @Valid @RequestBody RatingDTO ratingDTO
    ) {
        RatingDTO updatedRatingDTO = service.updatePassengerRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRatingDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RatingDTO> patchPassengerRating(
            @PathVariable String id,
            @Valid @RequestBody RatingPatchDTO ratingPatchDTO
    ) {
        RatingDTO ratingDTO = service.patchPassengerRating(id, ratingPatchDTO);
        return ResponseEntity.ok(ratingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingDTO> softDeletePassengerRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.softDeletePassengerRating(id);
        return ResponseEntity.ok(ratingDTO);
    }
}
