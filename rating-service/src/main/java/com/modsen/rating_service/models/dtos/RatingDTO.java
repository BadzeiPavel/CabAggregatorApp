package com.modsen.rating_service.models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private String id;

    @NotBlank(message = "Passenger ID cannot be empty")
    private String passengerId;

    @NotBlank(message = "Driver ID cannot be empty")
    private String driverId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private int rating;

    @Size(max = 255, message = "Comment cannot exceed 255 characters")
    private String comment;

    private LocalDateTime createdAt;

    private boolean isDeleted;
}
