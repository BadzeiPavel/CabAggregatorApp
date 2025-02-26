package com.modsen.rating_service.controllers;

import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.services.DriverRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings/drivers")
@RequiredArgsConstructor
public class DriverRatingController {

    private final DriverRatingService service;

    @PostMapping
    public ResponseEntity<RatingDTO> createDriverRating(@RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRatingDTO = service.createDriverRating(ratingDTO);
        return ResponseEntity.ok(createdRatingDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getDriverRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.getDriverRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{driverId}/all")
    public ResponseEntity<List<RatingDTO>> getDriverRatings(@PathVariable String driverId) {
        return ResponseEntity.ok(service.getDriverRatingsByDriverId(driverId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updateDriverRating(@PathVariable String id, @RequestBody RatingDTO ratingDTO) {
        RatingDTO updatedRatingDTO = service.updateDriverRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRatingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingDTO> softDeleteDriverRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.softDeleteDriverRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{driverId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getDriverRatingStatistic(@PathVariable String driverId) {
        return ResponseEntity.ok(service.getAverageRating(driverId));
    }
}
