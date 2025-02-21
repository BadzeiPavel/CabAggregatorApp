package com.modsen.rating_service.controllers;

import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.services.PassengerRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings/passengers")
@RequiredArgsConstructor
public class PassengerRatingController {

    private final PassengerRatingService passengerRatingService;

    @PostMapping
    public ResponseEntity<RatingDTO> savePassengerRating(@RequestBody RatingDTO ratingDTO) {
        RatingDTO savedRatingDTO = passengerRatingService.savePassengerRating(ratingDTO);
        return ResponseEntity.ok(savedRatingDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getPassengerRating(@PathVariable String id) {
        RatingDTO ratingDTO = passengerRatingService.getPassengerRatingDTO(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{passengerId}/all")
    public ResponseEntity<List<RatingDTO>> getAllPassengerRatings(@PathVariable String passengerId) {
        return ResponseEntity.ok(passengerRatingService.getAllPassengerRatingDTOsByPassengerId(passengerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updatePassengerRating(@PathVariable String id, @RequestBody RatingDTO ratingDTO) {
        RatingDTO updatedRatingDTO = passengerRatingService.updatePassengerRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRatingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingDTO> deletePassengerRating(@PathVariable String id) {
        RatingDTO ratingDTO = passengerRatingService.softDeletePassengerRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{passengerId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getPassengerRatingStatistic(@PathVariable String passengerId) {
        return ResponseEntity.ok(passengerRatingService.getAverageRating(passengerId));
    }
}
