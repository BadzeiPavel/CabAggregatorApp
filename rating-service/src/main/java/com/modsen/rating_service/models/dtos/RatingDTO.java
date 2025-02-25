package com.modsen.rating_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private String id;
    private String passengerId;
    private String driverId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private boolean isDeleted;
}
