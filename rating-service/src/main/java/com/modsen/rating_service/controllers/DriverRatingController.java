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

    private final DriverRatingService driverRatingService;

    @PostMapping
    public ResponseEntity<RatingDTO> saveDriverRating(@RequestBody RatingDTO ratingDTO) {
        RatingDTO savedRatingDTO = driverRatingService.saveDriverRating(ratingDTO);
        return ResponseEntity.ok(savedRatingDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getDriverRating(@PathVariable String id) {
        RatingDTO ratingDTO = driverRatingService.getDriverRatingDTO(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{driverId}/all")
    public ResponseEntity<List<RatingDTO>> getAllDriverRatings(@PathVariable String driverId) {
        return ResponseEntity.ok(driverRatingService.getAllDriverRatingDTOsByDriverId(driverId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updateDriverRating(@PathVariable String id, @RequestBody RatingDTO ratingDTO) {
        RatingDTO updatedRatingDTO = driverRatingService.updateDriverRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRatingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingDTO> deleteDriverRating(@PathVariable String id) {
        RatingDTO ratingDTO = driverRatingService.softDeleteDriverRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{driverId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getDriverRatingStatistic(@PathVariable String driverId) {
        return ResponseEntity.ok(driverRatingService.getAverageRating(driverId));
    }
}
