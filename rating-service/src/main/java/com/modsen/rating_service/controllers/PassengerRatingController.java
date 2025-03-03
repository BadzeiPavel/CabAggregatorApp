package com.modsen.rating_service.controllers;

import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.services.PassengerRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<GetAllResponseDTO<RatingDTO>> getPassengerRatings(@PathVariable String passengerId) {
        List<RatingDTO> ratings = service.getPassengerRatingsByPassengerId(passengerId);
        GetAllResponseDTO<RatingDTO> responseDTO = new GetAllResponseDTO<>(ratings);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updatePassengerRating(@PathVariable String id, @Valid
                                                                                    @RequestBody
                                                                                    RatingDTO ratingDTO) {
        RatingDTO updatedRatingDTO = service.updatePassengerRating(id, ratingDTO);
        return ResponseEntity.ok(updatedRatingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingDTO> softDeletePassengerRating(@PathVariable String id) {
        RatingDTO ratingDTO = service.softDeletePassengerRating(id);
        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/{passengerId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getPassengerRatingStatistic(@PathVariable String passengerId) {
        return ResponseEntity.ok(service.getAverageRating(passengerId));
    }
}
