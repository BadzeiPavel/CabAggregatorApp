package com.modsen.rating_service.unit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.mappers.RatingDTOMapper;
import com.modsen.rating_service.mappers.RatingMapper;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import com.modsen.rating_service.models.entities.DriverRating;
import com.modsen.rating_service.repositories.DriverRatingRepository;
import com.modsen.rating_service.services.DriverRatingService;
import com.modsen.rating_service.utils.CalculationUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import models.dtos.RatingStatisticResponseDTO;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import utils.PatchUtil;

@ExtendWith(MockitoExtension.class)
class DriverRatingServiceTest {

    @Mock
    private DriverRatingRepository repository;
    @Mock
    private RatingMapper ratingMapper;
    @Mock
    private RatingDTOMapper ratingDTOMapper;
    @Mock
    private CalculationUtil calculationUtil;
    
    @InjectMocks
    private DriverRatingService service;

    @Test
    void createDriverRating_Success() {
        // Given
        RatingDTO dto = new RatingDTO();
        dto.setRating(5);
        DriverRating entity = new DriverRating();
        
        when(ratingDTOMapper.toDriverRating(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(ratingMapper.toRatingDTO(entity)).thenReturn(dto);

        // When
        RatingDTO result = service.createDriverRating(dto);

        // Then
        assertThat(result).isEqualTo(dto);
        verify(repository).save(entity);
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.isDeleted()).isFalse();
    }

    @Test
    void getDriverRating_Success() {
        // Given
        String id = "1";
        DriverRating entity = new DriverRating();
        RatingDTO dto = new RatingDTO();
        
        when(repository.getDriverRatingById(id)).thenReturn(entity);
        when(ratingMapper.toRatingDTO(entity)).thenReturn(dto);

        // When
        RatingDTO result = service.getDriverRating(id);

        // Then
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getDriverRating_NotFound_ThrowsException() {
        // Given
        String id = "invalid";
        when(repository.getDriverRatingById(id))
                .thenThrow(new RatingNotFoundException("Driver rating not found"));

        // When & Then
        assertThatThrownBy(() -> service.getDriverRating(id))
                .isInstanceOf(RatingNotFoundException.class)
                .hasMessageContaining("Driver rating not found");
    }

    @Test
    void getPaginatedDriverRatingsByDriverId_ReturnsCorrectPage() {
        // Given
        String driverId = "driver1";
        PageRequest pageRequest = PageRequest.of(0, 10);
        DriverRating rating = new DriverRating();
        Page<DriverRating> page = new PageImpl<>(List.of(rating));
        RatingDTO dto = new RatingDTO();
        
        when(repository.findByDriverIdAndIsDeletedFalse(driverId, pageRequest)).thenReturn(page);
        when(ratingMapper.toRatingDTO(rating)).thenReturn(dto);

        // When
        GetAllPaginatedResponse<RatingDTO> response = 
            service.getPaginatedDriverRatingsByDriverId(driverId, pageRequest);

        // Then
        assertThat(response.getContent()).containsExactly(dto);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateDriverRating_Success() {
        // Given
        String id = "1";
        RatingDTO updateDTO = new RatingDTO();
        updateDTO.setRating(4);
        updateDTO.setComment("Updated comment");
        
        DriverRating existing = new DriverRating();
        existing.setRating(5);
        existing.setComment("Original comment");
        
        when(repository.getDriverRatingById(id)).thenReturn(existing);
        when(repository.save(existing)).thenReturn(existing);
        RatingDTO expectedDTO = new RatingDTO();
        when(ratingMapper.toRatingDTO(existing)).thenReturn(expectedDTO);

        // When
        RatingDTO result = service.updateDriverRating(id, updateDTO);

        // Then
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(existing.getRating()).isEqualTo(4);
        assertThat(existing.getComment()).isEqualTo("Updated comment");
    }

    @Test
    void patchDriverRating_PartialUpdate() {
        // Given
        String id = "1";
        RatingPatchDTO patchDTO = new RatingPatchDTO();
        patchDTO.setComment("New comment");
        
        DriverRating existing = new DriverRating();
        existing.setRating(5);
        existing.setComment("Old comment");
        
        when(repository.getDriverRatingById(id)).thenReturn(existing);
        when(repository.save(existing)).thenReturn(existing);
        RatingDTO expectedDTO = new RatingDTO();
        when(ratingMapper.toRatingDTO(existing)).thenReturn(expectedDTO);

        // When
        RatingDTO result = service.patchDriverRating(id, patchDTO);

        // Then
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(existing.getRating()).isEqualTo(5); // Rating remains unchanged
        assertThat(existing.getComment()).isEqualTo("New comment");
    }

    @Test
    void softDeleteDriverRating_Success() {
        // Given
        String id = "1";
        DriverRating entity = new DriverRating();
        entity.setDeleted(false);
        
        when(repository.getDriverRatingById(id)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        RatingDTO dto = new RatingDTO();
        when(ratingMapper.toRatingDTO(entity)).thenReturn(dto);

        // When
        RatingDTO result = service.softDeleteDriverRating(id);

        // Then
        assertThat(result).isEqualTo(dto);
        assertThat(entity.isDeleted()).isTrue();
    }

    @Test
    void getAverageRating_Success() {
        // Given
        String driverId = "driver1";
        DriverRating rating1 = new DriverRating();
        rating1.setRating(4);
        DriverRating rating2 = new DriverRating();
        rating2.setRating(5);
        List<DriverRating> ratings = List.of(rating1, rating2);
        
        when(repository.findByDriverIdAndIsDeletedFalse(driverId)).thenReturn(ratings);
        RatingDTO dto1 = new RatingDTO("random-id", "random-passenger-id", driverId, 4, "comment1", LocalDateTime.now(), false);
        RatingDTO dto2 = new RatingDTO("random-id", "random-passenger-id", driverId, 5, "comment2", LocalDateTime.now(), false);
        when(ratingMapper.toRatingDTO(rating1)).thenReturn(dto1);
        when(ratingMapper.toRatingDTO(rating2)).thenReturn(dto2);

        // When
        RatingStatisticResponseDTO result = service.getAverageRating(driverId);

        // Then
        assertThat(result.getRating()).isEqualTo(4.5);
        assertThat(result.getVotesNumber()).isEqualTo(2);
    }

    @Test
    void getAverageRating_NoRatings_ReturnsZero() {
        // Given
        String driverId = "driver1";

        when(repository.findByDriverIdAndIsDeletedFalse(driverId))
                .thenReturn(Collections.emptyList());

        // When
        RatingStatisticResponseDTO result = service.getAverageRating(driverId);

        // Then
        assertThat(result.getRating()).isEqualTo(0.0);
        assertThat(result.getVotesNumber()).isEqualTo(0);
    }
}