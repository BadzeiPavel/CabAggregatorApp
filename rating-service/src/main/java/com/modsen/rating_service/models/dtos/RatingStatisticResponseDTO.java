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

    public static RatingStatisticResponseDTO calculateRatingStatistics(List<DriverRating> driverRatings) {
        int sum = driverRatings.stream()
                .mapToInt(DriverRating::getRating)
                .sum();
        int votesNumber = driverRatings.size();
        double rating = (double) sum / votesNumber;
        String driverId = driverRatings.get(0).getDriverId();

        return new RatingStatisticResponseDTO(driverId, rating, votesNumber);
    }
}
