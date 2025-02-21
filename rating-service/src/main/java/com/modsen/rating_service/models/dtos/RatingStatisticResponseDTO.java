package com.modsen.rating_service.models.dtos;

import com.modsen.rating_service.models.entities.DriverRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatisticResponseDTO {
    private String driverId;
    private double rating;
    private long votesNumber;

    public static RatingStatisticResponseDTO calculateRatingStatistics(List<RatingDTO> ratings) {
        int sum = ratings.stream()
                .mapToInt(RatingDTO::getRating)
                .sum();
        int votesNumber = ratings.size();
        double rating = (double) sum / votesNumber;
        String driverId = ratings.get(0).getDriverId();

        return new RatingStatisticResponseDTO(driverId, rating, votesNumber);
    }
}
