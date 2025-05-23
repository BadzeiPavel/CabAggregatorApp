package com.modsen.rating_service.controllers;

import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.services.PassengerRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Passenger Rating Controller", description = "CRUD API for passenger rating")
@RestController
@RequestMapping("/api/v1/ratings/passengers")
@RequiredArgsConstructor
public class PassengerRatingController {

    private final PassengerRatingService service;

    @Operation(summary = "Create passenger rating")
    @PostMapping
    public ResponseEntity<RatingDTO> createPassengerRating(@Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRatingDTO = service.createPassengerRating(ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRatingDTO);
    }

    @Operation(summary = "Get passenger rating by id")
    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getPassengerRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.getPassengerRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @Operation(summary = "Get paginated passenger ratings by passenger_id")
    @GetMapping("/{passengerId}/all")
    public ResponseEntity<GetAllPaginatedResponse<RatingDTO>> getPaginatedPassengerRatings(
            @PathVariable String passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<RatingDTO> responseDTO =
                service.getPaginatedPassengerRatingsByPassengerId(passengerId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get passenger rating statistic by passenger_id")
    @GetMapping("/{passengerId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getPassengerRatingStatistic(@PathVariable String passengerId) {
        return ResponseEntity.ok(service.getAverageRating(passengerId));
    }

    @Operation(summary = "Update passenger rating by id")
    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updatePassengerRating(
            @PathVariable String id,
            @Valid @RequestBody RatingDTO ratingDTO
    ) {
        RatingDTO updatedRatingDTO = service.updatePassengerRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRatingDTO);
    }

    @Operation(summary = "Patch passenger rating by id")
    @PatchMapping("/{id}")
    public ResponseEntity<RatingDTO> patchPassengerRating(
            @PathVariable String id,
            @Valid @RequestBody RatingPatchDTO ratingPatchDTO
    ) {
        RatingDTO ratingDTO = service.patchPassengerRating(id, ratingPatchDTO);
        return ResponseEntity.ok(ratingDTO);
    }

    @Operation(summary = "Soft delete passenger rating by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<RatingDTO> softDeletePassengerRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.softDeletePassengerRating(id);
        return ResponseEntity.ok(ratingDTO);
    }
}
