package com.modsen.rating_service.integration.services;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import com.modsen.rating_service.models.entities.DriverRating;
import com.modsen.rating_service.repositories.DriverRatingRepository;
import com.modsen.rating_service.services.DriverRatingService;
import models.dtos.RatingStatisticResponseDTO;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class DriverRatingServiceIntegrationTest {

    @Autowired
    private DriverRatingService ratingService;

    @Autowired
    private DriverRatingRepository repository;

    private final String driverId = "driver-123";
    private final String passengerId = "passenger-456";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createDriverRating_ValidData_ShouldCreateRating() {
        RatingDTO request = RatingDTO.builder()
                .driverId(driverId)
                .passengerId(passengerId)
                .rating(5)
                .comment("Excellent service")
                .build();

        RatingDTO result = ratingService.createDriverRating(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.isDeleted()).isFalse();
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Excellent service");
    }

    @Test
    void getDriverRating_ExistingId_ShouldReturnRating() {
        DriverRating rating = createTestRating(4);
        RatingDTO result = ratingService.getDriverRating(rating.getId());

        assertThat(result.getId()).isEqualTo(rating.getId());
        assertThat(result.getDriverId()).isEqualTo(driverId);
    }

    @Test
    void getDriverRating_NonExistingId_ShouldThrowException() {
        String invalidId = "invalid-id";
        assertThatThrownBy(() -> ratingService.getDriverRating(invalidId))
                .isInstanceOf(RatingNotFoundException.class)
                .hasMessageContaining(invalidId);
    }

    @Test
    void getPaginatedDriverRatingsByDriverId_ShouldReturnPaginatedResults() {
        createTestRatings(5);
        PageRequest pageRequest = PageRequest.of(0, 3);

        GetAllPaginatedResponse<RatingDTO> response = 
            ratingService.getPaginatedDriverRatingsByDriverId(driverId, pageRequest);

        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(2);
    }

    @Test
    void updateDriverRating_ValidData_ShouldUpdateRating() {
        DriverRating existing = createTestRating(3);
        RatingDTO updateRequest = RatingDTO.builder()
                .rating(5)
                .comment("Updated comment")
                .build();

        RatingDTO result = ratingService.updateDriverRating(existing.getId(), updateRequest);

        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Updated comment");
    }

    @Test
    void patchDriverRating_PartialUpdate_ShouldUpdateSpecifiedFields() {
        DriverRating existing = createTestRating(3);
        RatingPatchDTO patchRequest = new RatingPatchDTO();
        patchRequest.setRating(4);

        RatingDTO result = ratingService.patchDriverRating(existing.getId(), patchRequest);

        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo(existing.getComment());
    }

    @Test
    void softDeleteDriverRating_ValidId_ShouldMarkAsDeleted() {
        DriverRating rating = createTestRating(4);
        RatingDTO result = ratingService.softDeleteDriverRating(rating.getId());

        assertThat(result.isDeleted()).isTrue();
        assertThat(repository.findById(rating.getId())).isPresent();
    }

    @Test
    void getAverageRating_WithMultipleRatings_ShouldCalculateCorrectly() {
        createTestRating(4);
        createTestRating(5);
        createTestRating(3);

        RatingStatisticResponseDTO response = ratingService.getAverageRating(driverId);

        assertThat(response.getRating()).isEqualTo(4.0);
        assertThat(response.getVotesNumber()).isEqualTo(3);
    }

    @Test
    void getAverageRating_NoRatings_ShouldReturnZero() {
        RatingStatisticResponseDTO response = ratingService.getAverageRating("non-existing-driver");
        
        assertThat(response.getRating()).isEqualTo(0.0);
        assertThat(response.getVotesNumber()).isEqualTo(0);
    }

    private DriverRating createTestRating(int rating) {
        return repository.save(DriverRating.builder()
                .driverId(driverId)
                .passengerId(passengerId)
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