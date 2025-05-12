package com.modsen.rating_service.integration.services;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import com.modsen.rating_service.models.entities.PassengerRating;
import com.modsen.rating_service.repositories.PassengerRatingRepository;
import com.modsen.rating_service.services.PassengerRatingService;
import models.dtos.RatingStatisticResponseDTO;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PassengerRatingServiceIntegrationTest {

    @Autowired
    private PassengerRatingService ratingService;

    @Autowired
    private PassengerRatingRepository repository;

    private final String passengerId = "passenger-123";
    private final String driverId = "driver-456";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createPassengerRating_ValidData_ShouldCreateRating() {
        RatingDTO request = RatingDTO.builder()
                .passengerId(passengerId)
                .driverId(driverId)
                .rating(4)
                .comment("Good passenger")
                .build();

        RatingDTO result = ratingService.createPassengerRating(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.isDeleted()).isFalse();
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Good passenger");
    }

    @Test
    void getPassengerRating_ExistingId_ShouldReturnRating() {
        PassengerRating rating = createTestRating(5);
        RatingDTO result = ratingService.getPassengerRating(rating.getId());

        assertThat(result.getId()).isEqualTo(rating.getId());
        assertThat(result.getPassengerId()).isEqualTo(passengerId);
    }

    @Test
    void getPassengerRating_NonExistingId_ShouldThrowException() {
        String invalidId = "invalid-id";
        assertThatThrownBy(() -> ratingService.getPassengerRating(invalidId))
                .isInstanceOf(RatingNotFoundException.class)
                .hasMessageContaining(invalidId);
    }

    @Test
    void getPaginatedPassengerRatingsByPassengerId_ShouldReturnPaginatedResults() {
        createTestRatings(5);
        PageRequest pageRequest = PageRequest.of(0, 3);

        GetAllPaginatedResponse<RatingDTO> response = 
            ratingService.getPaginatedPassengerRatingsByPassengerId(passengerId, pageRequest);

        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(2);
    }

    @Test
    void updatePassengerRating_ValidData_ShouldUpdateRating() {
        PassengerRating existing = createTestRating(3);
        RatingDTO updateRequest = RatingDTO.builder()
                .rating(5)
                .comment("Updated comment")
                .build();

        RatingDTO result = ratingService.updatePassengerRating(existing.getId(), updateRequest);

        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Updated comment");
    }

    @Test
    void patchPassengerRating_PartialUpdate_ShouldUpdateSpecifiedFields() {
        PassengerRating existing = createTestRating(3);
        RatingPatchDTO patchRequest = new RatingPatchDTO();
        patchRequest.setComment("Patched comment");

        RatingDTO result = ratingService.patchPassengerRating(existing.getId(), patchRequest);

        assertThat(result.getComment()).isEqualTo("Patched comment");
        assertThat(result.getRating()).isEqualTo(existing.getRating());
    }

    @Test
    void softDeletePassengerRating_ValidId_ShouldMarkAsDeleted() {
        PassengerRating rating = createTestRating(4);
        RatingDTO result = ratingService.softDeletePassengerRating(rating.getId());

        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    void getAverageRating_WithMultipleRatings_ShouldCalculateCorrectly() {
        createTestRating(4);
        createTestRating(5);
        createTestRating(3);

        RatingStatisticResponseDTO response = ratingService.getAverageRating(passengerId);

        assertThat(response.getRating()).isEqualTo(4.0);
        assertThat(response.getVotesNumber()).isEqualTo(3);
    }

    @Test
    void getAverageRating_NoRatings_ShouldReturnZero() {
        RatingStatisticResponseDTO response = ratingService.getAverageRating("non-existing-passenger");
        
        assertThat(response.getRating()).isEqualTo(0.0);
        assertThat(response.getVotesNumber()).isEqualTo(0);
    }

    private PassengerRating createTestRating(int rating) {
        return repository.save(PassengerRating.builder()
                .passengerId(passengerId)
                .driverId(driverId)
                .rating(rating)
                .comment("Test rating")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build());
    }

    private void createTestRatings(int count) {
        for (int i = 0; i < count; i++) {
            createTestRating(4);
        }
    }
}