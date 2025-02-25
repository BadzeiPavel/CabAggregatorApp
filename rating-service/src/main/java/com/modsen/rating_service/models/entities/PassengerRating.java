package com.modsen.rating_service.models.entities;

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
    private String passengerId;
    private String driverId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private boolean isDeleted;
}
