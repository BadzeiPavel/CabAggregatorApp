package com.modsen.rating_service.utils;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.models.dtos.RatingDTO;
import models.dtos.RatingStatisticResponseDTO;

import java.util.List;

public class CalculationUtil {

    public static RatingStatisticResponseDTO calculateRatingStatistics(List<RatingDTO> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return new RatingStatisticResponseDTO(0.0, 0);
        }
        int votesNumber = ratings.size();
        int sum = ratings.stream()
                .mapToInt(RatingDTO::getRating)
                .sum();
        double rating = (double) sum / votesNumber;

        return new RatingStatisticResponseDTO(rating, votesNumber);
    }
}
