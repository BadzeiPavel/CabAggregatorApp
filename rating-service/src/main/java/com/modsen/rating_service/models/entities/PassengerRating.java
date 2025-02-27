package com.modsen.rating_service.models.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Document(collection = "passenger_rating")
public class PassengerRating {
    @Id
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

    @NotNull(message = "CreatedAt cannot be null")
    private LocalDateTime createdAt;

    private boolean isDeleted;
}
