package models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatisticResponseDTO {
    @NotNull(message = "Rating cannot be null")
    @Positive(message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private double rating;

    @NotNull(message = "Votes number cannot be null")
    @Positive(message = "Votes number cannot be negative")
    private long votesNumber;
}
