package com.modsen.rating_service.controllers;

import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.services.DriverRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings/drivers")
@RequiredArgsConstructor
public class DriverRatingController {

    private final DriverRatingService service;

    @PostMapping
    public ResponseEntity<RatingDTO> createDriverRating(@Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRatingDTO = service.createDriverRating(ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRatingDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getDriverRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.getDriverRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{driverId}/all")
    public ResponseEntity<GetAllPaginatedResponseDTO<RatingDTO>> getPaginatedDriverRatings(
                                                                    @PathVariable String driverId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        GetAllPaginatedResponseDTO<RatingDTO> responseDTO =
                service.getPaginatedDriverRatingsByDriverId(driverId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{driverId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getDriverRatingStatistic(@PathVariable String driverId) {
        return ResponseEntity.ok(service.getAverageRating(driverId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updateDriverRating(@PathVariable String id,
                                                        @Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO updatedRatingDTO = service.updateDriverRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRatingDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RatingDTO> patchDriverRating(@PathVariable String id,
                                                       @Valid @RequestBody RatingPatchDTO ratingPatchDTO) {
        RatingDTO ratingDTO = service.patchDriverRating(id, ratingPatchDTO);
        return ResponseEntity.ok(ratingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingDTO> softDeleteDriverRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.softDeleteDriverRating(id);
        return ResponseEntity.ok(ratingDTO);
    }
}
