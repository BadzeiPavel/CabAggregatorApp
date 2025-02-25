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
    private double rating;
    private long votesNumber;

    public static RatingStatisticResponseDTO calculateRatingStatistics(List<RatingDTO> ratings) {
        int votesNumber = ratings.size();
        int sum = ratings.stream()
                .mapToInt(RatingDTO::getRating)
                .sum();
        double rating = (double) sum / votesNumber;

        return new RatingStatisticResponseDTO(rating, votesNumber);
    }
}
