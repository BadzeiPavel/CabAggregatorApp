package com.modsen.rating_service.repositories;

import com.modsen.rating_service.models.entities.PassengerRating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PassengerRatingRepository extends MongoRepository<PassengerRating, UUID> {
}
