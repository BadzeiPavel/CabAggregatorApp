package com.modsen.rating_service.models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatisticResponseDTO {
    @NotNull(message = "Rating cannot be null")
    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private double rating;

    @NotNull(message = "Votes number cannot be null")
    @Min(value = 0, message = "Votes number cannot be negative")
    private long votesNumber;
}
