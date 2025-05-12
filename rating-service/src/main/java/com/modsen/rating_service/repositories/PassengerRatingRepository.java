package com.modsen.rating_service.repositories;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.models.entities.PassengerRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerRatingRepository extends MongoRepository<PassengerRating, UUID> {
    List<PassengerRating> findByPassengerIdAndIsDeletedFalse(String id);
    Optional<PassengerRating> findByIdAndIsDeletedFalse(String id);

    Page<PassengerRating> findByPassengerIdAndIsDeletedFalse(String id, Pageable pageable);

    default PassengerRating getPassengerRatingById(String id) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new RatingNotFoundException("Rating entity with id='%s' not found".formatted(id)));
    }
}
